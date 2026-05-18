package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

/**
 * Resolves constructable Kotlin classes by calling their primary constructor with generated arguments.
 *
 * This resolver is the fallback for user-defined model types. A type is supported when its classifier is a
 * [KClass] with a primary constructor and is not one of the built-in scalar or collection types handled by earlier
 * resolvers in the chain. Although the name refers to data classes, any class with a primary constructor can be
 * resolved when it reaches this resolver.
 *
 * Constructor parameters are resolved as follows:
 * - If a property-specific factory exists for the parameter name, the factory value is used.
 * - If the parameter is required, its type is resolved through [ResolverChain].
 * - If the parameter has a default value, it is omitted so Kotlin can apply the default during `callBy`.
 *
 * Generic type arguments from the requested [KType] are mapped onto constructor parameter types before delegation,
 * allowing declarations such as `Box<String>` to resolve constructor parameters declared as `T`.
 *
 * @param propertyFactories Per-property factories keyed by owning class and constructor parameter name.
 * @param random Random source exposed to property factories through [FixtureContext].
 * @param nullableStrategy Nullable handling strategy exposed to property factories through [FixtureContext].
 * @param stringStrategy String generation strategy exposed to property factories through [FixtureContext].
 * @param collectionStrategy Collection sizing strategy exposed to property factories through [FixtureContext].
 */
class DataClassResolver(
    private val propertyFactories: Map<Pair<KClass<*>, String>, FixtureContext.() -> Any?> = emptyMap(),
    private val random: Random = Random.Default,
    private val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference,
    private val stringStrategy: StringStrategy = StringStrategy.Random(),
    private val collectionStrategy: CollectionStrategy = CollectionStrategy(),
) : TypeResolver {
    /**
     * Returns whether [type] can be instantiated by this resolver.
     *
     * The resolver requires a [KClass] classifier with a primary constructor. It deliberately rejects collection,
     * string, numeric, boolean, and character types because those are handled by more specific resolvers with
     * generation rules tailored to each type.
     *
     * @param type Type being checked by the resolver chain.
     * @return `true` when [type] is a constructable class that should be handled as a model object.
     */
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false

        return when {
            kClass.primaryConstructor == null -> false

            kClass.isSubclassOf(List::class) || kClass.isSubclassOf(Set::class) || kClass.isSubclassOf(Map::class) -> {
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
     * Creates an instance of [type] by resolving values for its primary constructor parameters.
     *
     * Property factories take precedence over generated values and receive a [FixtureContext] containing the current
     * random source, resolution stack, and configured generation strategies. Required parameters without custom
     * property factories are delegated back to [chain], while optional parameters are left out of the argument map so
     * their Kotlin default values are preserved.
     *
     * @param type Class type to instantiate.
     * @param chain Resolver chain used to generate required constructor parameter values.
     * @return A new instance of [type].
     * @throws IllegalStateException when [type] does not expose a primary constructor.
     */
    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val constructor = kClass.primaryConstructor
            ?: error("No primary constructor found for ${kClass.simpleName}")

        val typeArgMap = buildTypeArgMap(kClass, type)

        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack,
            nullableStrategy = nullableStrategy,
            stringStrategy = stringStrategy,
            collectionStrategy = collectionStrategy
        )

        val args = constructor.parameters
            .mapNotNull { param ->
                val propertyFactory = propertyFactories[kClass to param.name]
                if (propertyFactory != null) {
                    param to propertyFactory(context)
                } else if (!param.isOptional) {
                    val paramType = param.type
                    val resolvedType = typeArgMap[paramType.toString()] ?: paramType
                    param to chain.resolve(resolvedType)
                } else {
                    null
                }
            }.toMap()

        return constructor.callBy(args)
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
