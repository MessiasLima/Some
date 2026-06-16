package dev.appoutlet.some.core

import kotlin.random.Random

/**
 * Service-provider interface for contributing [TypeResolver]s to the fixture generation chain.
 *
 * Implementations are discovered at runtime via [java.util.ServiceLoader]. Third-party libraries
 * can ship their own provider to extend `Some` without requiring users to write configuration.
 */
interface TypeResolverProvider {
    /**
     * Creates resolvers to be appended to the built-in resolver chain.
     *
     * @param strategyProvider Provider of all configured generation strategies.
     * @param random Shared random source.
     * @return Ordered list of resolvers contributed by this provider.
     */
    fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<TypeResolver>
}
