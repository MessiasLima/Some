package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
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
        val resolver = ListResolver(config.collectionStrategy)
        val context = FixtureContext(Random.Default, emptyList())
        
        val result = resolver.resolve(typeOf<List<String>>(), context, chain)
        assertIs<List<*>>(result)
        assertTrue(result.size in 3..5)
    }
    
    @Test
    fun `ListResolver generates MutableList when requested`() {
        val resolver = ListResolver()
        val context = FixtureContext(Random.Default, emptyList())
        
        val result = resolver.resolve(typeOf<MutableList<String>>(), context, chain)
        assertIs<MutableList<*>>(result)
    }
    
    @Test
    fun `ListResolver canResolve detects List types`() {
        val resolver = ListResolver()
        assertTrue(resolver.canResolve(typeOf<List<String>>()))
    }
    
    @Test
    fun `ListResolver rejects non-List types`() {
        val resolver = ListResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
