package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JavaZonedDateTimeResolverTest {
    @Test
    fun `JavaZonedDateTimeResolver generates ZonedDateTime values`() {
        val resolver = JavaZonedDateTimeResolver(Random.Default)

        val result = resolver.resolve(typeOf<ZonedDateTime>(), defaultTestChain)
        assertIs<ZonedDateTime>(result)
    }

    @Test
    fun `JavaZonedDateTimeResolver canResolve detects ZonedDateTime type`() {
        val resolver = JavaZonedDateTimeResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<ZonedDateTime>()))
    }

    @Test
    fun `JavaZonedDateTimeResolver rejects other types`() {
        val resolver = JavaZonedDateTimeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()), "Should not resolve String")
        assertFalse(resolver.canResolve(typeOf<Int>()), "Should not resolve Int")
        assertFalse(resolver.canResolve(typeOf<Instant>()), "Should not resolve java.time.Instant")
    }
}
