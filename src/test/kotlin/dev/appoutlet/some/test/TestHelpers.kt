package dev.appoutlet.some.test

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain

/**
 * Default resolver chain for testing purposes.
 * Creates a chain with default configuration.
 */
val defaultTestChain: ResolverChain by lazy {
    val config = buildSomeConfig()
    val resolvers = config.buildResolvers()
    ResolverChain(resolvers, config[NullableStrategy::class])
}
