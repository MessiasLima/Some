package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LocalDateResolverTest {
    @Test
    fun `LocalDateResolver generates LocalDate values`() {
        val resolver = LocalDateResolver(Random.Default)

        val result = resolver.resolve(typeOf<LocalDate>(), defaultTestChain)
        assertIs<LocalDate>(result)
    }

    @Test
    fun `LocalDateResolver generates valid dates`() {
        val resolver = LocalDateResolver(Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<LocalDate>(), defaultTestChain) as LocalDate
            assertTrue(result.year >= LocalDate.MIN.year, "Year should be at least LocalDate.MIN.year")
            assertTrue(result.year <= LocalDate.MAX.year, "Year should be at most LocalDate.MAX.year")
            assertTrue(result.dayOfYear in 1..Year.of(result.year).length(), "Day of year should be valid for the year")
        }
    }

    @Test
    fun `LocalDateResolver canResolve detects LocalDate type`() {
        val resolver = LocalDateResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<LocalDate>()))
    }

    @Test
    fun `LocalDateResolver does NOT match LocalDateTime`() {
        val resolver = LocalDateResolver(Random.Default)
        assertFalse(
            resolver.canResolve(typeOf<LocalDateTime>()),
            "LocalDateResolver should NOT match LocalDateTime type"
        )
    }

    @Test
    fun `LocalDateResolver rejects non-LocalDate types`() {
        val resolver = LocalDateResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
