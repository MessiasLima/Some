package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.DefaultValueStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Resolves types that have user-registered type factories.
 *
 * This resolver is intentionally first in the default resolver chain so explicit user configuration takes precedence
 * over every built-in resolver. A type is supported when its [KClass] is present in [typeFactories]. When resolved,
 * the matching type factory is invoked directly and no further resolver delegation happens for that type.
 *
 * Type factories receive a [FixtureContext] containing the active random source, resolution stack, and generation
 * strategies. This lets user code produce values that still respect the current fixture configuration.
 *
 * @param typeFactories Map of classes to user-provided type factory functions.
 * @param random Random source exposed to type factories through [FixtureContext].
 * @param nullableStrategy Nullable handling strategy exposed to type factories through [FixtureContext].
 * @param stringStrategy String generation strategy exposed to type factories through [FixtureContext].
 * @param collectionStrategy Collection sizing strategy exposed to type factories through [FixtureContext].
 * @param defaultValueStrategy Default value strategy exposed to type factories through [FixtureContext].
 */
class CustomTypeFactoryResolver(
    private val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?>,
    private val random: Random,
    private val nullableStrategy: NullableStrategy,
    private val stringStrategy: StringStrategy,
    private val collectionStrategy: CollectionStrategy,
    private val defaultValueStrategy: DefaultValueStrategy
) : TypeResolver {
    /**
     * Returns whether [type] has a registered type factory.
     *
     * Matching is based on the type classifier's [KClass]. Generic arguments and nullability are not part of the key,
     * so a type factory registered for `ListWrapper::class` applies to all requested `ListWrapper<T>` variants that
     * reach this resolver.
     *
     * @param type Type being checked by the resolver chain.
     * @return `true` when [typeFactories] contains a type factory for [type]'s classifier.
     */
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass in typeFactories
    }

    /**
     * Invokes the registered type factory for [type].
     *
     * The generated [FixtureContext] uses an immutable snapshot of [chain]'s current resolution stack so type factories
     * can inspect the active resolution path without mutating resolver state.
     *
     * @param type Type to resolve.
     * @param chain Resolver chain providing the current resolution stack.
     * @return The value produced by the registered type factory, or `null` if no type factory is found.
     */
    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val kClass = type.classifier as KClass<*>
        val typeFactory = typeFactories[kClass] ?: return null

        // Bridge: Create FixtureContext for user type factories with an immutable copy of the stack
        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack, // This returns an immutable snapshot
            nullableStrategy = nullableStrategy,
            stringStrategy = stringStrategy,
            collectionStrategy = collectionStrategy,
            defaultValueStrategy = defaultValueStrategy
        )

        return typeFactory.invoke(context)
    }
}
