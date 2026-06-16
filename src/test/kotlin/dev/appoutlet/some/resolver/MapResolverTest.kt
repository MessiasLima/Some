package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.DefaultStrategyProvider
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
        val strategyProvider = DefaultStrategyProvider(
            mapOf(CollectionStrategy::class to CollectionStrategy(2..4))
        )
        val resolver = MapResolver(strategyProvider, Random.Default)

        val result = resolver.resolve(typeOf<Map<String, Int>>(), defaultTestChain)
        assertIs<Map<*, *>>(result)
        assertTrue(result.size in 2..4)
    }

    @Test
    fun `MapResolver generates MutableMap when requested`() {
        val resolver = MapResolver(DefaultStrategyProvider(), Random.Default)

        val result = resolver.resolve(typeOf<MutableMap<String, Int>>(), defaultTestChain)
        assertIs<MutableMap<*, *>>(result)
    }

    @Test
    fun `MapResolver canResolve detects Map types`() {
        val resolver = MapResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Map<String, Int>>()))
    }

    @Test
    fun `MapResolver rejects non-Map types`() {
        val resolver = MapResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
