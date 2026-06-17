package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import kotlin.random.Random

/**
 * Test [TypeResolverProvider] that throws during resolver creation.
 *
 * Used to verify that `SomeConfig.buildResolvers()` gracefully skips misbehaving providers.
 */
@AutoService
class FailingTypeResolverProvider : TypeResolverProvider {
    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver> = error("Simulated provider failure")
}
