package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.get
import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.exception.SomeInstantiationException
import dev.appoutlet.some.logging.logger
import java.lang.reflect.Modifier
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

/**
 * Resolves constructable Kotlin classes by calling their constructors with generated arguments.
 *
 * This resolver is the fallback for user-defined model types. A type is supported when its classifier is a
 * [KClass] with at least one constructor (primary or secondary), is not abstract, and is not one of the
 * built-in scalar or collection types handled by earlier resolvers in the chain.
 *
 * Constructors are tried in declaration order. The first constructor whose arguments can be fully resolved
 * wins. If all constructors fail, a [SomeInstantiationException] is thrown summarizing each failure.
 *
 * [SomeCircularReferenceException] is never caught by this resolver; it always propagates so callers receive
 * accurate circular-reference diagnostics.
 *
 * Constructor parameters are resolved as follows:
 * - If a property-specific factory exists for the parameter name, the factory value is used.
 * - If the parameter is required, its type is resolved through [ResolverChain].
 * - If the parameter has a default value, it is omitted so Kotlin can apply the default during `callBy`.
 *
 * Generic type arguments from the requested [KType] are mapped onto constructor parameter types before delegation,
 * allowing declarations such as `Box<String>` to resolve constructor parameters declared as `T`.
 *
 * @param strategyProvider Provides all registered generation strategies to property factories through [FixtureContext].
 * @param propertyFactories Per-property factories keyed by owning class and constructor parameter name.
 * @param random Random source exposed to property factories through [FixtureContext].
 */
class ClassResolver(
    private val strategyProvider: StrategyProvider,
    private val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
    private val random: Random = Random.Default,
) : TypeResolver {
    private val logger by logger()
    private val defaultValueStrategy = strategyProvider.get<DefaultValueStrategy>() ?: DefaultValueStrategy.default

    /**
     * Returns whether [type] can be instantiated by this resolver.
     *
     * The resolver requires a [KClass] classifier with at least one constructor that is not abstract.
     * It deliberately rejects collection, string, numeric, boolean, and character types because those are
     * handled by more specific resolvers with generation rules tailored to each type.
     *
     * @param type Type being checked by the resolver chain.
     * @return `true` when [type] is a constructable class that should be handled as a model object.
     */
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false

        return when {
            kClass.constructors.isEmpty() -> false

            Modifier.isAbstract(kClass.java.modifiers) -> false

            kClass.isSubclassOf(List::class) || kClass.isSubclassOf(Set::class) || kClass.isSubclassOf(
                Map::class
            ) -> {
                false
            }

            kClass.isSubclassOf(String::class) -> false

            kClass.isSubclassOf(Number::class) -> false

            kClass.isSubclassOf(Boolean::class) -> false

            kClass.isSubclassOf(Char::class) -> false

            else -> true
        }
    }

    /**
     * Creates an instance of [type] by trying each constructor in order.
     *
     * Property factories take precedence over generated values and receive a [FixtureContext] containing the current
     * random source, resolution stack, and configured generation strategies. Required parameters without custom
     * property factories are delegated back to [chain], while optional parameters are left out of the argument map so
     * their Kotlin default values are preserved.
     *
     * If a constructor succeeds, its result is returned immediately. If all constructors fail, a
     * [SomeInstantiationException] is thrown with details about each failure.
     *
     * [SomeCircularReferenceException] is re-thrown immediately and is never absorbed into the failure summary,
     * ensuring that circular-reference diagnostics propagate to the caller.
     *
     * @param type Class type to instantiate.
     * @param chain Resolver chain used to generate required constructor parameter values.
     * @return A new instance of [type].
     * @throws SomeInstantiationException when all constructors fail to instantiate [type].
     */
    @Suppress("TooGenericExceptionCaught")
    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val failures = mutableListOf<String>()

        for (constructor in kClass.constructors) {
            try {
                constructor.isAccessible = true
                val result = tryConstructor(constructor, kClass, type, chain)
                return requireNotNull(result) { "Constructor returned null" }
            } catch (e: SomeCircularReferenceException) {
                throw e
            } catch (e: Exception) {
                logger.d(e) { "Constructor failed for ${kClass.simpleName}" }
                failures.add(
                    "Constructor ${constructor.parameters.map { it.name }}: ${e.message ?: e::class.simpleName}"
                )
            }
        }

        logger.w { "All constructors failed for ${kClass.simpleName}" }
        throw SomeInstantiationException(kClass, failures)
    }

    private fun tryConstructor(
        constructor: KFunction<*>,
        kClass: KClass<*>,
        type: KType,
        chain: ResolverChain,
    ): Any? {
        val args = constructor.parameters.mapNotNull { param ->
            val propertyFactory = propertyFactories[kClass to param.name]
            val shouldGenerate = !param.isOptional ||
                defaultValueStrategy == DefaultValueStrategy.Generate

            when {
                propertyFactory != null -> resolveByPropertyFactory(chain, param, propertyFactory)
                shouldGenerate -> resolveByResolvers(kClass, type, param, chain)
                else -> null
            }
        }.toMap()

        return constructor.callBy(args)
    }

    private fun resolveByResolvers(
        kClass: KClass<*>,
        type: KType,
        param: KParameter,
        chain: ResolverChain
    ): Pair<KParameter, Any?> {
        val typeArgMap = buildTypeArgMap(kClass, type)
        val paramType = param.type
        val resolvedType = typeArgMap[paramType.toString()] ?: paramType
        return param to chain.resolve(resolvedType)
    }

    private fun resolveByPropertyFactory(
        chain: ResolverChain,
        param: KParameter,
        propertyFactory: FixtureContext.() -> Any?
    ): Pair<KParameter, Any?> {
        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack,
            strategyProvider = strategyProvider,
        )

        return param to propertyFactory(context)
    }

    /**
     * Builds a mapping from class type parameter names to the concrete type arguments requested by [type].
     *
     * The map is keyed by parameter name because constructor parameter types for generic classes are represented as
     * type variables such as `T`. Star projections or missing projection types fall back to the type parameter's own
     * upper-bound-backed type so resolution can continue with the most specific information available.
     *
     * @param kClass Class declaring the type parameters.
     * @param type Requested type, potentially containing concrete generic arguments.
     * @return Mapping from type parameter name to concrete [KType].
     */
    private fun buildTypeArgMap(kClass: KClass<*>, type: KType): Map<String, KType> {
        return kClass.typeParameters
            .zip(type.arguments)
            .associate { (param, projection) ->
                param.name to (projection.type ?: param.createType(emptyList(), false))
            }
    }
}
