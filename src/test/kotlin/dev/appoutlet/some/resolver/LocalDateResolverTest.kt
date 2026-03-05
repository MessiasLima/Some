package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `LocalDateResolver generates dates in year 2024`() {
        val resolver = LocalDateResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<LocalDate>(), defaultTestChain) as LocalDate
            assertEquals(result.year, 2024, "Expected year 2024, got ${result.year}")
            assertTrue(result.dayOfYear in 1..366, "Day of year should be between 1 and 366")
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
        assertTrue(!resolver.canResolve(typeOf<LocalDateTime>()), 
            "LocalDateResolver should NOT match LocalDateTime type")
    }
    
    @Test
    fun `LocalDateResolver rejects non-LocalDate types`() {
        val resolver = LocalDateResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
