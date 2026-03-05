package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BooleanResolverTest {
    @Test
    fun `BooleanResolver generates boolean values`() {
        val resolver = BooleanResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Boolean>(), defaultTestChain)
        assertIs<Boolean>(result)
    }
    
    @Test
    fun `BooleanResolver canResolve detects Boolean type`() {
        val resolver = BooleanResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Boolean>()))
    }
    
    @Test
    fun `BooleanResolver rejects non-Boolean types`() {
        val resolver = BooleanResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
