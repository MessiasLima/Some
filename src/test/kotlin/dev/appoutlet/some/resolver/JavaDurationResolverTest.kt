package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import java.time.Duration
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JavaDurationResolverTest {
    @Test
    fun `JavaDurationResolver generates Duration values`() {
        val resolver = JavaDurationResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<Duration>(), defaultTestChain)
        assertIs<Duration>(result)
    }
    
    @Test
    fun `JavaDurationResolver generates durations within valid range`() {
        val resolver = JavaDurationResolver(Random.Default)
        
        repeat(100) {
            val result = resolver.resolve(typeOf<Duration>(), defaultTestChain) as Duration
            assertTrue(result.seconds >= 0, "Duration should be non-negative")
            assertTrue(result.seconds < 86400, "Duration should be less than 1 day (86400 seconds)")
        }
    }
    
    @Test
    fun `JavaDurationResolver canResolve detects Duration type`() {
        val resolver = JavaDurationResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<Duration>()))
    }
    
    @Test
    fun `JavaDurationResolver rejects non-Duration types`() {
        val resolver = JavaDurationResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
        assertFalse(resolver.canResolve(typeOf<kotlin.time.Duration>()))
    }
}
