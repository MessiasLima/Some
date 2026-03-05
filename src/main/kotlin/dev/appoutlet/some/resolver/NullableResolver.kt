package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class NullableResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type.isMarkedNullable

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any? {
        return when (val strategy = context.config.nullableStrategy) {
            is NullableStrategy.AlwaysNull -> null
            is NullableStrategy.NeverNull -> createNonNullInstance(type, chain, context)
            is NullableStrategy.Random -> {
                // Use the probability supplied by the Random strategy instance
                if (context.random.nextDouble() < strategy.probability) {
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
