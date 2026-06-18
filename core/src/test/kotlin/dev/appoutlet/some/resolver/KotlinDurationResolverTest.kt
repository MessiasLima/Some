package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class KotlinDurationResolverTest {
    @Test
    fun `KotlinDurationResolver generates Duration values`() {
        val resolver = KotlinDurationResolver(Random.Default)

        val result = resolver.resolve(typeOf<Duration>(), defaultTestChain)
        assertIs<Duration>(result)
    }

    @Test
    fun `KotlinDurationResolver generates durations within valid range`() {
        val resolver = KotlinDurationResolver(Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<Duration>(), defaultTestChain) as Duration
            assertTrue(result.inWholeSeconds >= 0, "Duration should be non-negative")
            assertTrue(result.inWholeSeconds < 1.days.inWholeSeconds, "Duration should be less than 1 day")
        }
    }

    @Test
    fun `KotlinDurationResolver canResolve detects Duration type`() {
        val resolver = KotlinDurationResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Duration>()))
    }

    @Test
    fun `KotlinDurationResolver rejects non-Duration types`() {
        val resolver = KotlinDurationResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
        assertFalse(resolver.canResolve(typeOf<java.time.Duration>()))
    }
}
