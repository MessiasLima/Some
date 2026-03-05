package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StringResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `StringResolver generates random string of length 8`() {
        val resolver = StringResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            stringStrategy = StringStrategy.Random
        })
        
        val result = resolver.resolve(typeOf<String>(), context, chain)
        assertIs<String>(result)
        assertTrue(result.length == 8)
    }
    
    @Test
    fun `StringResolver generates UUID when configured`() {
        val resolver = StringResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            stringStrategy = StringStrategy.Uuid
        })
        
        val result = resolver.resolve(typeOf<String>(), context, chain)
        assertIs<String>(result)
        assertTrue(result.contains("-"))
    }
    
    @Test
    fun `StringResolver canResolve detects String type`() {
        val resolver = StringResolver()
        assertTrue(resolver.canResolve(typeOf<String>()))
    }
    
    @Test
    fun `StringResolver rejects non-String types`() {
        val resolver = StringResolver()
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
