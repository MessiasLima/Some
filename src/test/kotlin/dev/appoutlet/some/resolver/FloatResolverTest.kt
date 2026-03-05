package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloatResolverTest {
    @Test
    fun `FloatResolver generates float values`() {
        val resolver = FloatResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Float>(), defaultTestChain)
        assertIs<Float>(result)
    }
    
    @Test
    fun `FloatResolver generates values between 0 and 1`() {
        val resolver = FloatResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in 0.0f..<1.0f, "Expected value between 0.0 and 1.0, got $result")
        }
    }
    
    @Test
    fun `FloatResolver canResolve detects Float type`() {
        val resolver = FloatResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Float>()))
    }
    
    @Test
    fun `FloatResolver rejects non-Float types`() {
        val resolver = FloatResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Double>()))
    }
}
