package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Resolves types that have user-registered type factories.
 *
 * This resolver is intentionally first in the default resolver chain so explicit user configuration takes precedence
 * over every built-in resolver. Type-level factories keyed by the complete [KType] take precedence over class-level
 * factories keyed by [KClass]. When resolved, the matching type factory is invoked directly and no further resolver
 * delegation happens for that type.
 *
 * Matching precedence is:
 * 1. An exact [KType] match in [kTypeFactories].
 * 2. For a nullable request, the same type with only its outer nullability removed.
 * 3. The classifier [KClass] in [typeFactories].
 *
 * Type factories receive a [FixtureContext] containing the active random source, resolution stack, and strategy
 * provider. This lets user code produce values that still respect the current fixture configuration.
 *
 * @param strategyProvider Provides all registered generation strategies to type factories through [FixtureContext].
 * @param typeFactories Map of classes to user-provided type factory functions.
 * @param random Random source exposed to type factories through [FixtureContext].
 * @param kTypeFactories Map of complete types to user-provided type factory functions.
 */
class CustomFactoryResolver(
    private val strategyProvider: StrategyProvider,
    private val typeFactories: Map<KClass<*>, FixtureContext.() -> Any?>,
    private val random: Random,
    private val kTypeFactories: Map<KType, FixtureContext.() -> Any?> = emptyMap(),
) : Resolver {
    /**
     * Returns whether [type] has a registered type factory.
     *
     * A complete [KType] match is checked first. A nullable request also falls back to the same type without its
     * outer nullability, which lets a factory for `List<Number>` apply to `List<Number>?`. Finally, the classifier
     * [KClass] is checked so the existing class-level factory behavior is preserved.
     *
     * @param type Type being checked by the resolver chain.
     * @return `true` when an exact type, non-null fallback, or classifier factory matches [type].
     */
    override fun canResolve(type: KType): Boolean = findFactory(type) != null

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
        val typeFactory = findFactory(type) ?: return null

        val context = FixtureContext(
            random = random,
            resolutionStack = chain.stack.toList(),
            strategyProvider = strategyProvider,
        )

        return typeFactory.invoke(context)
    }

    /**
     * Finds the factory that applies to [type] using the documented precedence.
     */
    private fun findFactory(type: KType): (FixtureContext.() -> Any?)? {
        val exactMatch = kTypeFactories[type]
        val nonNullMatch = if (type.isMarkedNullable) {
            kTypeFactories[type.withNullability(false)]
        } else {
            null
        }
        val classMatch = (type.classifier as? KClass<*>)?.let { typeFactories[it] }

        return exactMatch ?: nonNullMatch ?: classMatch
    }
}
