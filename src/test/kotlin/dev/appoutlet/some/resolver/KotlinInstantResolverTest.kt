package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

class KotlinInstantResolverTest {
    @Test
    fun `KotlinInstantResolver generates Instant values`() {
        val resolver = KotlinInstantResolver(Random.Default)

        val result = resolver.resolve(typeOf<Instant>(), defaultTestChain)
        assertIs<Instant>(result)
    }

    @Test
    fun `KotlinInstantResolver generates instants within valid range`() {
        val resolver = KotlinInstantResolver(Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<Instant>(), defaultTestChain) as Instant
            assertTrue(result >= Instant.DISTANT_PAST, "Instant should be after or at distant past")
            assertTrue(result < Instant.DISTANT_FUTURE, "Instant should be before distant future")
        }
    }

    @Test
    fun `KotlinInstantResolver canResolve detects Instant type`() {
        val resolver = KotlinInstantResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Instant>()))
    }

    @Test
    fun `KotlinInstantResolver rejects non-Instant types`() {
        val resolver = KotlinInstantResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `KotlinInstantResolver rejects Java Instant type`() {
        val resolver = KotlinInstantResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<java.time.Instant>()))
    }
}
