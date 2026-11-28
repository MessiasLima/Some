package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.buildSomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class VarianceBox<T>

class NullableResolverTest {
    @Test
    fun `NullableResolver with AlwaysNull strategy returns null`() {
        val resolver = NullableResolver(
            DefaultStrategyProvider(mapOf(NullableStrategy::class to NullableStrategy.AlwaysNull)),
            Random.Default
        )

        repeat(1000) {
            val result = resolver.resolve(typeOf<String?>(), defaultTestChain)
            assertNull(result)
        }
    }

    @Test
    fun `NullableResolver with NeverNull strategy generates non-null values`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.NeverNull)
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers, config[NullableStrategy::class]).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }

    @Test
    fun `NullableResolver with Random strategy can return null or value`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random())
        }
        val resolvers = config.buildResolvers()

        val results = (1..100).map {
            ResolverChain(
                resolvers,
                config[NullableStrategy::class]
            ).resolve(typeOf<String?>())
        }
        val hasNull = results.any { it == null }
        val hasValue = results.any { it != null }
        assertTrue(hasNull && hasValue)
    }

    @Test
    fun `NullableResolver with Random strategy and probability 0_0 always returns non-null`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random(probability = 0.0))
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers, config[NullableStrategy::class]).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }

    @Test
    fun `NullableResolver with Random strategy and probability 1_0 always returns null`() {
        val config = buildSomeConfig {
            strategy(NullableStrategy.Random(probability = 1.0))
        }
        val resolvers = config.buildResolvers()

        repeat(1000) {
            val result = ResolverChain(resolvers, config[NullableStrategy::class]).resolve(typeOf<String?>())
            assertNull(result)
        }
    }

    @Test
    fun `NullableResolver canResolve detects nullable types`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<String?>()))
    }

    @Test
    fun `NullableResolver rejects non-nullable types`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
    }

    @Test
    fun `NullableResolver caches the non-null type per nullable type`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)

        val first = resolver.createNonNullType(typeOf<List<String?>?>())
        val second = resolver.createNonNullType(typeOf<List<String?>?>())

        assertEquals(typeOf<List<String?>>(), first)
        assertEquals(first, second)
        assertEquals(1, resolver.cachedNonNullTypeCount)
    }

    @Test
    fun `NullableResolver keeps distinct nullable types independent`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)

        resolver.createNonNullType(typeOf<String?>())
        resolver.createNonNullType(typeOf<List<String?>?>())

        assertEquals(2, resolver.cachedNonNullTypeCount)
    }

    @Test
    fun `NullableResolver preserves nested nullability and variance`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)

        assertEquals(
            typeOf<VarianceBox<out String?>>(),
            resolver.createNonNullType(typeOf<VarianceBox<out String?>?>()),
        )
    }

    @Test
    fun `NullableResolver cache is safe when shared across threads`() {
        val resolver = NullableResolver(DefaultStrategyProvider(), Random.Default)
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable { resolver.createNonNullType(typeOf<List<String?>?>()) }
            }
            val results = executor.invokeAll(tasks).map { it.get() }

            assertTrue(results.all { it == typeOf<List<String?>>() })
            assertEquals(1, resolver.cachedNonNullTypeCount)
        } finally {
            executor.shutdownNow()
        }
    }
}
