package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.full.createType

public class NullableResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type.isMarkedNullable

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any? {
        return when (context.config.nullableStrategy) {
            NullableStrategy.AlwaysNull -> null
            NullableStrategy.NeverNull -> {
                val nonNullType = createNonNullType(type)
                chain.resolve(nonNullType, context)
            }
            NullableStrategy.Random -> {
                if (context.random.nextBoolean()) {
                    null
                } else {
                    val nonNullType = createNonNullType(type)
                    chain.resolve(nonNullType, context)
                }
            }
            NullableStrategy.RandomWeighted -> {
                if (context.random.nextDouble() < context.config.nullableWeightedProbability) {
                    null
                } else {
                    val nonNullType = createNonNullType(type)
                    chain.resolve(nonNullType, context)
                }
            }
        }
    }
    
    private fun createNonNullType(type: KType): KType {
        return type.classifier?.createType(type.arguments, false) ?: type
    }
}
