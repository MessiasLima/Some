package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DoubleResolverTest {
    @Test
    fun `DoubleResolver generates double values`() {
        val resolver = DoubleResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Double>(), defaultTestChain)
        assertIs<Double>(result)
    }
    
    @Test
    fun `DoubleResolver generates values between 0 and 1`() {
        val resolver = DoubleResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Double>(), defaultTestChain) as Double
            assertTrue(result in 0.0..<1.0, "Expected value between 0.0 and 1.0, got $result")
        }
    }
    
    @Test
    fun `DoubleResolver canResolve detects Double type`() {
        val resolver = DoubleResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Double>()))
    }
    
    @Test
    fun `DoubleResolver rejects non-Double types`() {
        val resolver = DoubleResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Float>()))
    }
}
