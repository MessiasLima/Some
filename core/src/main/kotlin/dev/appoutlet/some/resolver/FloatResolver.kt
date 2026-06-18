package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.FloatStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [Float] types using the active [FloatStrategy].
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used for generating float values.
 */
class FloatResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : TypeResolver {
    private val floatStrategy = strategyProvider.get<FloatStrategy>() ?: FloatStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<Float>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val range = floatStrategy.range
        if (range.start == range.endInclusive) return range.start

        return random.nextDouble(range.start.toDouble(), range.endInclusive.toDouble()).toFloat()
    }
}
