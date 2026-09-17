package dev.appoutlet.some.retrofit

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.retrofit.resolver.ResponseResolver
import kotlin.random.Random

/**
 * Contributes Retrofit-specific resolvers discovered through [java.util.ServiceLoader].
 */
class RetrofitResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> = listOf(ResponseResolver())
}
