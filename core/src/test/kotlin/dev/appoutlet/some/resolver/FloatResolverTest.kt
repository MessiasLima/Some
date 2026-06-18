package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.FloatStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class FloatResolverTest {
    @Test
    fun `FloatResolver generates float values`() {
        val resolver = FloatResolver(SomeConfig(), Random.Default)

        val result = resolver.resolve(typeOf<Float>(), defaultTestChain)
        assertIs<Float>(result)
    }

    @Test
    fun `FloatResolver generates values between 0 and 1 by default`() {
        val resolver = FloatResolver(SomeConfig(), Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in 0.0f..1.0f, "Expected value between 0.0 and 1.0, got $result")
        }
    }

    @Test
    fun `FloatResolver respects FloatStrategy range`() {
        val config = SomeConfig(strategies = mapOf(FloatStrategy::class to FloatStrategy(10.0f..20.0f)))
        val resolver = FloatResolver(config, Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in 10.0f..20.0f, "Expected value between 10.0 and 20.0, got $result")
        }
    }

    @Test
    fun `FloatResolver handles zero-width range`() {
        val config = SomeConfig(strategies = mapOf(FloatStrategy::class to FloatStrategy(5.0f)))
        val resolver = FloatResolver(config, Random.Default)

        repeat(10) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertEquals(5.0f, result)
        }
    }

    @Test
    fun `FloatResolver canResolve detects Float type`() {
        val resolver = FloatResolver(SomeConfig(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Float>()))
    }

    @Test
    fun `FloatResolver rejects non-Float types`() {
        val resolver = FloatResolver(SomeConfig(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Double>()))
    }
}
