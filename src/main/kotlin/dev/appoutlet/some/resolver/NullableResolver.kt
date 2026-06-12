package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Resolves nullable Kotlin types according to the configured [NullableStrategy].
 *
 * - **NullOnCircularReference** – delegates to the chain to resolve normally (might be null if a cycle is detected).
 * - **AlwaysNull** – always returns `null`.
 * - **NeverNull** – always resolves a non-null value.
 * - **Random** – returns `null` based on the strategy's probability.
 *
 * @param nullableStrategy Strategy for resolving nullable types. Defaults to [NullableStrategy.default] when null.
 * @param random Random source used by [NullableStrategy.Random].
 */
class NullableResolver(
    nullableStrategy: NullableStrategy?,
    private val random: Random
) : TypeResolver {
    private val nullableStrategy = nullableStrategy ?: NullableStrategy.default

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
        return when (nullableStrategy) {
            is NullableStrategy.NullOnCircularReference -> createNonNullInstance(type, chain)
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
     *
     * Circular references are still detected by [ResolverChain], which decides whether the current strategy allows
     * the circular value to be represented as `null`.
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
