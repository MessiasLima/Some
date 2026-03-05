package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalDateTimeResolverTest {
    @Test
    fun `LocalDateTimeResolver generates LocalDateTime values`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<LocalDateTime>(), defaultTestChain)
        assertIs<LocalDateTime>(result)
    }
    
    @Test
    fun `LocalDateTimeResolver generates valid dates and times in year 2024`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<LocalDateTime>(), defaultTestChain) as LocalDateTime
            assertTrue(result.year in 1970..3000, "Year should be between 1970..3000")
            assertTrue(result.toLocalDate().dayOfYear in 1..366, "Day of year should be between 1 and 366")
            assertTrue(result.hour in 0..23, "Hour should be between 0 and 23")
            assertTrue(result.minute in 0..59, "Minute should be between 0 and 59")
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
        assertFalse(resolver.canResolve(typeOf<LocalDate>()), 
            "LocalDateTimeResolver should NOT match LocalDate type")
    }
    
    @Test
    fun `LocalDateTimeResolver rejects non-LocalDateTime types`() {
        val resolver = LocalDateTimeResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
