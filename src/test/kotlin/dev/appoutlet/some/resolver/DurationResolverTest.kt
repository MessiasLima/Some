package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.Duration
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DurationResolverTest {
    @Test
    fun `DurationResolver generates Duration values`() {
        val resolver = DurationResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Duration>(), defaultTestChain)
        assertIs<Duration>(result)
    }
    
    @Test
    fun `DurationResolver generates durations within valid range`() {
        val resolver = DurationResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Duration>(), defaultTestChain) as Duration
            assertTrue(result.seconds >= 0, "Duration should be non-negative")
            assertTrue(result.seconds < 86400, "Duration should be less than 1 day (86400 seconds)")
        }
    }
    
    @Test
    fun `DurationResolver canResolve detects Duration type`() {
        val resolver = DurationResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Duration>()))
    }
    
    @Test
    fun `DurationResolver rejects non-Duration types`() {
        val resolver = DurationResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
        assertTrue(!resolver.canResolve(typeOf<Long>()))
    }
}
