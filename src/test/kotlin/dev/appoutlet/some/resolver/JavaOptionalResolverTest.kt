package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.some
import java.util.Optional
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JavaOptionalResolverTest {
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
        // Since Optional<OptionalCircular> is treated as OptionalCircular?,
        // and we are at the first level of circularity for OptionalCircular,
        // it should resolve to a non-null OptionalCircular, but wrapped in Optional.

        // Wait, if it's the first time it sees OptionalCircular, it should resolve it.
        // If it's a circular reference, it should be null.

        // Let's re-examine how ResolverChain works.
        // resolve(OptionalCircular)
        //   -> ClassResolver resolves OptionalCircular
        //     -> resolve(Optional<OptionalCircular>)
        //       -> JavaOptionalResolver
        //         -> resolve(OptionalCircular?)
        //           -> NullableResolver
        //             -> resolve(OptionalCircular)
        //               -> CIRCULAR REFERENCE!

        // If NullableStrategy is NullOnCircularReference, resolve(OptionalCircular?) returns null.
        // So Optional.ofNullable(null) is Optional.empty().

        assertFalse(result.optional.isPresent, "Circular reference should result in empty optional")
    }

    @Test
    fun `JavaOptionalResolver canResolve detects Optional types`() {
        val resolver = JavaOptionalResolver(NullableStrategy.default, kotlin.random.Random.Default)
        assertTrue(resolver.canResolve(typeOf<Optional<String>>()))
        assertTrue(resolver.canResolve(typeOf<Optional<Int>>()))
    }

    @Test
    fun `JavaOptionalResolver rejects non-Optional types`() {
        val resolver = JavaOptionalResolver(NullableStrategy.default, kotlin.random.Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<List<String>>()))
    }
}
