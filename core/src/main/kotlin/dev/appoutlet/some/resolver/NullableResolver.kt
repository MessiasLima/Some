package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import dev.appoutlet.some.exception.SomeCircularReferenceException
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

/**
 * Resolves nullable Kotlin types according to the configured [NullableStrategy].
 *
 * - **NullOnCircularReference** – delegates to the chain to resolve non-null value; returns `null`
 *   if a circular reference exception occurs for a circular type.
 * - **AlwaysNull** – always returns `null`.
 * - **NeverNull** – always resolves a non-null value.
 * - **Random** – returns `null` based on the strategy's probability.
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used by [NullableStrategy.Random].
 */
class NullableResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val nullableStrategy = strategyProvider.get<NullableStrategy>() ?: NullableStrategy.default

    override fun canResolve(type: KType): Boolean = type.isMarkedNullable

    /**
     * Resolves [type] according to the active [NullableStrategy].
     *
     * Strategies that choose a concrete value resolve the non-null version of [type] through [chain].
     *
     * @param type Nullable type to resolve.
     * @param chain Resolver chain used to create non-null values when needed.
     * @return `null` or a generated non-null value for [type].
     */
    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val isCircular = chain.stack.dropLast(1).any {
            it.withNullability(false) == type.withNullability(false)
        }

        return when (nullableStrategy) {
            is NullableStrategy.NullOnCircularReference -> {
                try {
                    createNonNullInstance(type, chain)
                } catch (e: SomeCircularReferenceException) {
                    if (isCircular) {
                        null
                    } else {
                        throw e
                    }
                }
            }
            is NullableStrategy.AlwaysNull -> null
            is NullableStrategy.NeverNull -> createNonNullInstance(type, chain)
            is NullableStrategy.Random -> {
                if (random.nextDouble() < nullableStrategy.probability) {
                    null
                } else {
                    createNonNullInstance(type, chain)
                }
            }
        }
    }

    /**
     * Resolves the non-null version of [type] through [chain].
     */
    private fun createNonNullInstance(
        type: KType,
        chain: ResolverChain
    ): Any? {
        val nonNullType = createNonNullType(type)
        return chain.resolve(nonNullType)
    }

    /**
     * Creates a copy of [type] with the nullable marker removed.
     */
    private fun createNonNullType(type: KType): KType {
        return type.classifier?.createType(type.arguments, false) ?: type
    }
}
