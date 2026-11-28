package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

enum class TestColor { RED, GREEN, BLUE }

enum class TestSize { SMALL, LARGE }

class EnumResolverTest {
    @Test
    fun `EnumResolver picks random enum value`() {
        val resolver = EnumResolver(Random.Default)

        val result = resolver.resolve(typeOf<TestColor>(), defaultTestChain)
        assertIs<TestColor>(result)
    }

    @Test
    fun `EnumResolver caches constants per enum class`() {
        val resolver = EnumResolver(Random.Default)

        resolver.resolve(typeOf<TestColor>(), defaultTestChain)
        resolver.resolve(typeOf<TestColor>(), defaultTestChain)

        assertEquals(1, resolver.cachedEnumCount)
    }

    @Test
    fun `EnumResolver keeps distinct enum caches independent`() {
        val resolver = EnumResolver(Random.Default)

        resolver.resolve(typeOf<TestColor>(), defaultTestChain)
        resolver.resolve(typeOf<TestSize>(), defaultTestChain)

        assertEquals(2, resolver.cachedEnumCount)
    }

    @Test
    fun `EnumResolver randomly selects a constant on each resolution`() {
        val resolver = EnumResolver(Random(42))

        val results = List(100) {
            resolver.resolve(typeOf<TestColor>(), defaultTestChain) as TestColor
        }

        assertTrue(results.toSet().size > 1)
        assertEquals(1, resolver.cachedEnumCount)
    }

    @Test
    fun `EnumResolver canResolve detects enum types`() {
        val resolver = EnumResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<TestColor>()))
    }

    @Test
    fun `EnumResolver rejects non-enum types`() {
        val resolver = EnumResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }
}
