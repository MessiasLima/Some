package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NumberResolverTest {
    @Test
    fun `NumberResolver generates a value of type Number`() {
        val resolver = NumberResolver(Random.Default)
        val result = resolver.resolve(typeOf<Number>(), defaultTestChain)
        assertIs<Number>(result)
    }

    @Test
    fun `NumberResolver canResolve detects Number type`() {
        val resolver = NumberResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Number>()))
    }

    @Test
    fun `NumberResolver rejects non-Number types`() {
        val resolver = NumberResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
        assertFalse(resolver.canResolve(typeOf<Double>()))
        assertFalse(resolver.canResolve(typeOf<Float>()))
        assertFalse(resolver.canResolve(typeOf<Short>()))
        assertFalse(resolver.canResolve(typeOf<Byte>()))
    }
}
