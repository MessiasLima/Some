package dev.appoutlet.some.test

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.Strategy
import dev.appoutlet.some.resolver.nullable.NullableStrategy

/**
 * Default resolver chain for testing purposes.
 * Creates a chain with default configuration.
 */
val defaultTestChain: ResolverChain by lazy {
    val config = buildSomeConfig()
    val resolvers = config.buildResolvers()
    ResolverChain(resolvers, config[NullableStrategy::class])
}
