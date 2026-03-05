package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ListResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `ListResolver generates list with correct size`() {
        val config = SomeConfig().apply {
            collectionStrategy = CollectionStrategy(3..5)
        }
        val chain = config.buildChain()
        val resolver = ListResolver(CollectionStrategy(3..5), Random.Default)
        
        val result = resolver.resolve(typeOf<List<String>>(), chain)
        assertIs<List<*>>(result)
        assertTrue(result.size in 3..5)
    }
    
    @Test
    fun `ListResolver generates MutableList when requested`() {
        val resolver = ListResolver(CollectionStrategy(), Random.Default)
        
        val result = resolver.resolve(typeOf<MutableList<String>>(), chain)
        assertIs<MutableList<*>>(result)
    }
    
    @Test
    fun `ListResolver canResolve detects List types`() {
        val resolver = ListResolver(CollectionStrategy(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<List<String>>()))
    }
    
    @Test
    fun `ListResolver rejects non-List types`() {
        val resolver = ListResolver(CollectionStrategy(), Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
