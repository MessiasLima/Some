package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BigDecimalResolverTest {
    @Test
    fun `BigDecimalResolver generates BigDecimal values`() {
        val resolver = BigDecimalResolver(Random.Default)
        val result = resolver.resolve(typeOf<BigDecimal>(), defaultTestChain)
        assertIs<BigDecimal>(result)
        // Verify it's a valid BigDecimal by performing an operation
        val doubled = result.multiply(BigDecimal.TWO)
        assertIs<BigDecimal>(doubled)
    }

    @Test
    fun `BigDecimalResolver canResolve detects BigDecimal type`() {
        val resolver = BigDecimalResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<BigDecimal>()))
    }

    @Test
    fun `BigDecimalResolver rejects non-BigDecimal types`() {
        val resolver = BigDecimalResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Double>()))
    }
}
