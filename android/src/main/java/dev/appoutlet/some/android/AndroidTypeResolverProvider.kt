package dev.appoutlet.some.android

import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import kotlin.random.Random

class AndroidTypeResolverProvider : TypeResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random
    ): List<TypeResolver> {
        return emptyList()
    }
}
