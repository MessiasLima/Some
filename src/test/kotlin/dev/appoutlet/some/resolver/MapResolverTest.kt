package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MapResolverTest {
    @Test
    fun `MapResolver generates map with correct size`() {
        val config = SomeConfig().apply {
            collectionStrategy = CollectionStrategy(2..4)
        }
        val resolvers = config.buildResolvers()
        val resolver = MapResolver(CollectionStrategy(2..4), Random.Default)
        
        val result = resolver.resolve(typeOf<Map<String, Int>>(), ResolverChain(resolvers))
        assertIs<Map<*, *>>(result)
        assertTrue(result.size in 2..4)
    }
    
    @Test
    fun `MapResolver generates MutableMap when requested`() {
        val resolver = MapResolver(CollectionStrategy(), Random.Default)
        
        val result = resolver.resolve(typeOf<MutableMap<String, Int>>(), defaultTestChain)
        assertIs<MutableMap<*, *>>(result)
    }
    
    @Test
    fun `MapResolver canResolve detects Map types`() {
        val resolver = MapResolver(CollectionStrategy(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Map<String, Int>>()))
    }
    
    @Test
    fun `MapResolver rejects non-Map types`() {
        val resolver = MapResolver(CollectionStrategy(), Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
