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

class LocalDateTimeResolverTest {
    @Test
    fun `LocalDateTimeResolver generates LocalDateTime values`() {
        val resolver = LocalDateTimeResolver(Random.Default)

        val result = resolver.resolve(typeOf<LocalDateTime>(), defaultTestChain)
        assertIs<LocalDateTime>(result)
    }

    @Test
    fun `LocalDateTimeResolver generates valid dates and times`() {
        val resolver = LocalDateTimeResolver(Random.Default)

        repeat(100) {
            val result = resolver.resolve(typeOf<LocalDateTime>(), defaultTestChain) as LocalDateTime
            assertTrue(result.year >= LocalDate.MIN.year, "Year should be at least LocalDate.MIN.year")
            assertTrue(result.year <= LocalDate.MAX.year, "Year should be at most LocalDate.MAX.year")
            assertTrue(
                result.toLocalDate().dayOfYear in 1..Year.of(result.year).length(),
                "Day of year should be valid for the year"
            )
            assertTrue(
                result.hour < 24,
                "Hour should be between 0 and 23"
            )
            assertTrue(
                result.minute < 60,
                "Minute should be between 0 and 59"
            )
        }
    }

    @Test
    fun `LocalDateTimeResolver canResolve detects LocalDateTime type`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<LocalDateTime>()))
    }

    @Test
    fun `LocalDateTimeResolver does NOT match LocalDate`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        assertFalse(
            resolver.canResolve(typeOf<LocalDate>()),
            "LocalDateTimeResolver should NOT match LocalDate type"
        )
    }

    @Test
    fun `LocalDateTimeResolver rejects non-LocalDateTime types`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
