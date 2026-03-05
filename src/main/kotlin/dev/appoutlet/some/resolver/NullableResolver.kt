package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Resolves nullable Kotlin types according to the configured [NullableStrategy].
 *
 * - **AlwaysNull** – always returns `null`.
 * - **NeverNull** – always resolves a non‑null value.
 * - **Random** – returns `null` based on the strategy's probability.
 */
class NullableResolver(
    private val nullableStrategy: NullableStrategy = NullableStrategy.Random()
) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type.isMarkedNullable

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any? {
        return when (nullableStrategy) {
            is NullableStrategy.AlwaysNull -> null
            is NullableStrategy.NeverNull -> createNonNullInstance(type, chain, context)
            is NullableStrategy.Random -> {
                // Use the probability supplied by the Random strategy instance
                if (context.random.nextDouble() < nullableStrategy.probability) {
                    null
                } else {
                    createNonNullInstance(type, chain, context)
                }
            }
        }
    }

    private fun createNonNullInstance(
        type: KType,
        chain: ResolverChain,
        context: FixtureContext
    ): Any? {
        val nonNullType = createNonNullType(type)
        return chain.resolve(nonNullType, context)
    }

    private fun createNonNullType(type: KType): KType {
        return type.classifier?.createType(type.arguments, false) ?: type
    }
}
