package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.math.BigInteger
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BigIntegerResolverTest {
    @Test
    fun `BigIntegerResolver generates BigInteger values`() {
        val resolver = BigIntegerResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<BigInteger>(), defaultTestChain)
        assertIs<BigInteger>(result)
        // Verify it's a valid BigInteger by performing an operation
        val doubled = result.multiply(BigInteger.TWO)
        assertIs<BigInteger>(doubled)
    }
    
    @Test
    fun `BigIntegerResolver canResolve detects BigInteger type`() {
        val resolver = BigIntegerResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<BigInteger>()))
    }
    
    @Test
    fun `BigIntegerResolver rejects non-BigInteger types`() {
        val resolver = BigIntegerResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
