package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShortResolverTest {
    @Test
    fun `ShortResolver generates short values`() {
        val resolver = ShortResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Short>(), defaultTestChain)
        assertIs<Short>(result)
    }
    
    @Test
    fun `ShortResolver canResolve detects Short type`() {
        val resolver = ShortResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Short>()))
    }
    
    @Test
    fun `ShortResolver rejects non-Short types`() {
        val resolver = ShortResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Byte>()))
    }
}
