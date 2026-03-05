package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class IntResolverTest {
    @Test
    fun `IntResolver generates int values`() {
        val resolver = IntResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Int>(), defaultTestChain)
        assertIs<Int>(result)
    }
    
    @Test
    fun `IntResolver canResolve detects Int type`() {
        val resolver = IntResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Int>()))
    }
    
    @Test
    fun `IntResolver rejects non-Int types`() {
        val resolver = IntResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
