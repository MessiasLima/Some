package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MapResolverTest {
    @Test
    fun `MapResolver generates map with correct size`() {
        val config = SomeConfig().strategy(CollectionStrategy(2..4))
        val resolvers = config.buildResolvers()
        val resolver = MapResolver(config, Random.Default)

        val result = resolver.resolve(typeOf<Map<String, Int>>(), ResolverChain(resolvers))
        assertIs<Map<*, *>>(result)
        assertTrue(result.size in 2..4)
    }

    @Test
    fun `MapResolver generates MutableMap when requested`() {
        val strategyProvider: StrategyProvider = SomeConfig()
        val resolver = MapResolver(strategyProvider, Random.Default)

        val result = resolver.resolve(typeOf<MutableMap<String, Int>>(), defaultTestChain)
        assertIs<MutableMap<*, *>>(result)
    }

    @Test
    fun `MapResolver canResolve detects Map types`() {
        val resolver = MapResolver(SomeConfig(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Map<String, Int>>()))
    }

    @Test
    fun `MapResolver rejects non-Map types`() {
        val resolver = MapResolver(SomeConfig(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
