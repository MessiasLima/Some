package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.some
import java.util.Optional
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OptionalResolverTest {
    @Test
    fun `JavaOptionalResolver generates Optional values`() {
        val result: Optional<String> = some()
        assertNotNull(result)
        assertTrue(result.isPresent)
        assertIs<String>(result.get())
    }

    @Test
    fun `JavaOptionalResolver with AlwaysNull strategy returns empty Optional`() {
        val result: Optional<String> = some {
            strategy(NullableStrategy.AlwaysNull)
        }
        assertNotNull(result)
        assertFalse(result.isPresent)
    }

    @Test
    fun `JavaOptionalResolver with NeverNull strategy always returns present Optional`() {
        repeat(100) {
            val result: Optional<String> = some {
                strategy(NullableStrategy.NeverNull)
            }
            assertNotNull(result)
            assertTrue(result.isPresent)
        }
    }

    @Test
    fun `JavaOptionalResolver with Random strategy can return empty or present Optional`() {
        val results = (1..100).map {
            some<Optional<String>> {
                strategy(NullableStrategy.Random(probability = 0.5))
            }
        }
        assertTrue(results.any { !it.isPresent })
        assertTrue(results.any { it.isPresent })
    }

    data class OptionalCircular(val optional: Optional<OptionalCircular>)

    @Test
    fun `JavaOptionalResolver handles circular references with empty Optional`() {
        val result: OptionalCircular = some {
            strategy(NullableStrategy.NullOnCircularReference)
        }
        assertNotNull(result)
        assertFalse(result.optional.isPresent, "Circular reference should result in empty optional")
    }

    @Test
    fun `JavaOptionalResolver canResolve detects Optional types`() {
        val resolver = OptionalResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Optional<String>>()))
        assertTrue(resolver.canResolve(typeOf<Optional<Int>>()))
    }

    @Test
    fun `JavaOptionalResolver rejects non-Optional types`() {
        val resolver = OptionalResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
    }
}
