package dev.appoutlet.some.core

import kotlin.random.Random

/**
 * Service-provider interface for contributing [Resolver]s to the fixture generation chain.
 *
 * Implementations are discovered at runtime via [java.util.ServiceLoader]. Third-party libraries
 * can ship their own provider to extend `Some` without requiring users to write configuration.
 *
 * Contributed resolvers are inserted between [NullableResolver][dev.appoutlet.some.resolver.NullableResolver]
 * and the built-in resolvers, giving them priority over built-in type handling while still allowing
 * user-registered type factories ([CustomTypeFactoryResolver][dev.appoutlet.some.resolver.CustomFactoryResolver])
 * to take precedence.
 */
interface ResolverProvider {
    /**
     * Creates resolvers to be contributed to the fixture generation chain.
     *
     * @param strategyProvider Provider of all configured generation strategies.
     * @param random Shared random source.
     * @return Ordered list of resolvers contributed by this provider.
     */
    fun createResolvers(
        strategyProvider: StrategyProvider,
        random: Random,
    ): List<Resolver>
}
