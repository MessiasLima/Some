package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MapResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `MapResolver generates map with correct size`() {
        val config = SomeConfig().apply {
            collectionStrategy = CollectionStrategy(2..4)
        }
        val chain = config.buildChain()
        val resolver = MapResolver(config.collectionStrategy)
        val context = FixtureContext(Random.Default, emptyList())
        
        val result = resolver.resolve(typeOf<Map<String, Int>>(), context, chain)
        assertIs<Map<*, *>>(result)
        assertTrue(result.size in 2..4)
    }
    
    @Test
    fun `MapResolver generates MutableMap when requested`() {
        val resolver = MapResolver()
        val context = FixtureContext(Random.Default, emptyList())
        
        val result = resolver.resolve(typeOf<MutableMap<String, Int>>(), context, chain)
        assertIs<MutableMap<*, *>>(result)
    }
    
    @Test
    fun `MapResolver canResolve detects Map types`() {
        val resolver = MapResolver()
        assertTrue(resolver.canResolve(typeOf<Map<String, Int>>()))
    }
    
    @Test
    fun `MapResolver rejects non-Map types`() {
        val resolver = MapResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
