package dev.appoutlet.some.test

import com.fueledbycaffeine.autoservice.AutoService
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.TypeResolverProvider
import kotlin.random.Random

/**
 * Test [TypeResolverProvider] that fails during class instantiation (init block).
 *
 * Simulates a class-loading failure (e.g., NoClassDefFoundError from a missing optional dependency)
 * that would cause [java.util.ServiceLoader.iterator.next] to throw. Used to verify that a faulty
 * provider does not discard other valid providers discovered in the same iteration.
 */
@AutoService
class FailingLoadTypeResolverProvider : TypeResolverProvider {
    init {
        error("Simulated class loading failure")
    }

    override fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver> = throw UnsupportedOperationException()
}
