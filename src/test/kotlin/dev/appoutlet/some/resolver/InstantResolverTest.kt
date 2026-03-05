package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.Instant
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InstantResolverTest {
    @Test
    fun `InstantResolver generates Instant values`() {
        val resolver = InstantResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Instant>(), defaultTestChain)
        assertIs<Instant>(result)
    }
    
    @Test
    fun `InstantResolver generates instants within valid range`() {
        val resolver = InstantResolver(Random.Default)
        val epochStart = Instant.ofEpochSecond(0)
        val year2100 = Instant.ofEpochSecond(4102444800)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Instant>(), defaultTestChain) as Instant
            assertTrue(result.isAfter(epochStart) || result == epochStart, "Instant should be after or at epoch")
            assertTrue(result.isBefore(year2100), "Instant should be before year 2100")
        }
    }
    
    @Test
    fun `InstantResolver canResolve detects Instant type`() {
        val resolver = InstantResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Instant>()))
    }
    
    @Test
    fun `InstantResolver rejects non-Instant types`() {
        val resolver = InstantResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
