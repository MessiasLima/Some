package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverProvider
import kotlin.random.Random

/**
 * Test [ResolverProvider] that throws during resolver creation.
 *
 * Used to verify that `SomeConfig.buildResolvers()` gracefully skips misbehaving providers.
 */
@AutoService
class FailingResolverProvider : ResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver> = error("Simulated provider failure")
}
