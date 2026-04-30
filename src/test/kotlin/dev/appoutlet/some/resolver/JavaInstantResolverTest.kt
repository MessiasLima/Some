package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.Instant
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JavaInstantResolverTest {
    @Test
    fun `JavaInstantResolver generates Instant values`() {
        val resolver = JavaInstantResolver(Random.Default)

        val result = resolver.resolve(typeOf<Instant>(), defaultTestChain)
        assertIs<Instant>(result)
    }

    @Test
    fun `JavaInstantResolver generates instants within valid range`() {
        val resolver = JavaInstantResolver(Random.Default)
        val epochStart = Instant.ofEpochSecond(Instant.MIN.epochSecond)
        val year2100 = Instant.ofEpochSecond(Instant.MAX.epochSecond)

        repeat(100) {
            val result = resolver.resolve(typeOf<Instant>(), defaultTestChain) as Instant
            assertTrue(result.isAfter(epochStart) || result == epochStart, "Instant should be after or at epoch")
            assertTrue(result.isBefore(year2100), "Instant should be before year 2100")
        }
    }

    @Test
    fun `JavaInstantResolver canResolve detects Instant type`() {
        val resolver = JavaInstantResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Instant>()))
    }

    @Test
    fun `JavaInstantResolver rejects non-Instant types`() {
        val resolver = JavaInstantResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
        assertFalse(resolver.canResolve(typeOf<kotlin.time.Instant>()))
    }
}
