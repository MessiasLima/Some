package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import dev.appoutlet.some.core.StrategyProvider
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * Test [ResolverProvider] that records discovery and resolver-creation calls.
 *
 * Returns no resolvers so it can observe [dev.appoutlet.some.config.SomeConfig] discovery without affecting the
 * generated resolver chain.
 */
@AutoService
class CountingResolverProvider : ResolverProvider {
    init {
        instantiations.incrementAndGet()
    }

    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> {
        createCalls.incrementAndGet()
        strategyProviders.add(strategyProvider)
        randoms.add(random)
        return emptyList()
    }

    companion object {
        val instantiations = AtomicInteger()
        val createCalls = AtomicInteger()
        val strategyProviders = CopyOnWriteArrayList<StrategyProvider>()
        val randoms = CopyOnWriteArrayList<Random>()
    }
}
