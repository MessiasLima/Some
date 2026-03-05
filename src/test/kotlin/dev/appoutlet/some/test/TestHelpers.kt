package dev.appoutlet.some.test

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain

/**
 * Default resolver chain for testing purposes.
 * Creates a chain with default configuration.
 */
val defaultTestChain: ResolverChain by lazy {
    val resolvers = SomeConfig().buildResolvers()
    ResolverChain(resolvers)
}
