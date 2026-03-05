package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CharResolverTest {
    @Test
    fun `CharResolver generates char values`() {
        val resolver = CharResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Char>(), defaultTestChain)
        assertIs<Char>(result)
    }
    
    @Test
    fun `CharResolver generates lowercase letters only`() {
        val resolver = CharResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Char>(), defaultTestChain) as Char
            assertTrue(result in 'a'..'z', "Expected char in range 'a'..'z', got '$result'")
        }
    }
    
    @Test
    fun `CharResolver canResolve detects Char type`() {
        val resolver = CharResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Char>()))
    }
    
    @Test
    fun `CharResolver rejects non-Char types`() {
        val resolver = CharResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Byte>()))
    }
}
