package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.FloatStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class FloatResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val floatStrategy = strategyProvider.get<FloatStrategy>() ?: FloatStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<Float>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val range = floatStrategy.range
        return if (range.start == range.endInclusive) {
            range.start
        } else {
            random.nextDouble(
                from = range.start.toDouble(),
                until = range.endInclusive.toDouble(),
            ).toFloat()
        }
    }
}
