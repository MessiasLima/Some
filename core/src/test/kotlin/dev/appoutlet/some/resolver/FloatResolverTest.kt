package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.FloatStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FloatResolverTest {
    @Test
    fun `FloatResolver generates float values`() {
        val resolver = FloatResolver(DefaultStrategyProvider(), Random.Default)

        val result = resolver.resolve(typeOf<Float>(), defaultTestChain)
        assertIs<Float>(result)
    }

    @Test
    fun `FloatResolver generates values between 0 and 1 by default`() {
        val resolver = FloatResolver(DefaultStrategyProvider(), Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in 0.0f..<1.0f, "Expected value between 0.0 and 1.0, got $result")
        }
    }

    @Test
    fun `FloatResolver honors a custom range`() {
        val resolver = FloatResolver(
            DefaultStrategyProvider(mapOf(FloatStrategy::class to FloatStrategy(0.0f..10.0f))),
            Random.Default
        )

        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in 0.0f..<10.0f, "Expected value between 0.0 and 10.0, got $result")
        }
    }

    @Test
    fun `FloatResolver honors a negative range`() {
        val resolver = FloatResolver(
            DefaultStrategyProvider(mapOf(FloatStrategy::class to FloatStrategy(-5.0f..-1.0f))),
            Random.Default
        )

        repeat(100) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertTrue(result in -5.0f..<-1.0f, "Expected value between -5.0 and -1.0, got $result")
        }
    }

    @Test
    fun `FloatResolver always returns fixed value for pinned strategy`() {
        val resolver = FloatResolver(
            DefaultStrategyProvider(mapOf(FloatStrategy::class to FloatStrategy(5.0f))),
            Random.Default
        )

        repeat(50) {
            val result = resolver.resolve(typeOf<Float>(), defaultTestChain) as Float
            assertEquals(5.0f, result)
        }
    }

    @Test
    fun `FloatResolver canResolve detects Float type`() {
        val resolver = FloatResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Float>()))
    }

    @Test
    fun `FloatResolver rejects non-Float types`() {
        val resolver = FloatResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Double>()))
    }

    @Test
    fun `FloatStrategy integrates with SomeConfig`() {
        val config = buildSomeConfig {
            strategy(FloatStrategy(0.0f..10.0f))
        }
        val resolvers = config.buildResolvers()
        val chain = ResolverChain(resolvers, config[NullableStrategy::class])

        repeat(50) {
            val result = chain.resolve(typeOf<Float>()) as Float
            assertTrue(result in 0.0f..<10.0f, "Expected value between 0.0 and 10.0, got $result")
        }
    }
}
