package dev.appoutlet.some.android

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import dev.appoutlet.some.core.StrategyProvider
import kotlin.random.Random

class AndroidResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random
    ): List<Resolver> {
        return emptyList()
    }
}
