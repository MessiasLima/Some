package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.StringStrategy
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
        val resolver = StringResolver(StringStrategy.Random, Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), chain)
        assertIs<String>(result)
        assertTrue(result.length == 8)
    }
    
    @Test
    fun `StringResolver generates UUID when configured`() {
        val resolver = StringResolver(StringStrategy.Uuid, Random.Default)
        
        val result = resolver.resolve(typeOf<String>(), chain)
        assertIs<String>(result)
        assertTrue(result.contains("-"))
    }
    
    @Test
    fun `StringResolver canResolve detects String type`() {
        val resolver = StringResolver(StringStrategy.Random, Random.Default)
        assertTrue(resolver.canResolve(typeOf<String>()))
    }
    
    @Test
    fun `StringResolver rejects non-String types`() {
        val resolver = StringResolver(StringStrategy.Random, Random.Default)
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
