package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

// Test value classes
@JvmInline
value class UserId(val id: Int)

@JvmInline
value class Email(val value: String)

@JvmInline
value class Score(val points: Long)

class ValueClassResolverTest {
    @Test
    fun `ValueClassResolver generates value class instances`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<UserId>(), defaultTestChain)
        assertIs<UserId>(result)
    }

    @Test
    fun `ValueClassResolver resolves underlying Int value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<UserId>(), defaultTestChain) as UserId
        assertIs<Int>(result.id)
    }

    @Test
    fun `ValueClassResolver resolves underlying String value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<Email>(), defaultTestChain) as Email
        assertIs<String>(result.value)
        assertTrue(result.value.isNotEmpty())
    }

    @Test
    fun `ValueClassResolver resolves underlying Long value correctly`() {
        val resolver = ValueClassResolver()

        val result = resolver.resolve(typeOf<Score>(), defaultTestChain) as Score
        assertIs<Long>(result.points)
    }

    @Test
    fun `ValueClassResolver canResolve detects value classes`() {
        val resolver = ValueClassResolver()
        assertTrue(resolver.canResolve(typeOf<UserId>()))
        assertTrue(resolver.canResolve(typeOf<Email>()))
        assertTrue(resolver.canResolve(typeOf<Score>()))
    }

    @Test
    fun `ValueClassResolver rejects non-value classes`() {
        val resolver = ValueClassResolver()
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<Long>()))
    }

    @Test
    fun `ValueClassResolver handles multiple value classes with same underlying type`() {
        val resolver = ValueClassResolver()

        // Both use Int, but are different types
        val userId = resolver.resolve(typeOf<UserId>(), defaultTestChain) as UserId
        assertIs<UserId>(userId)

        // Score uses Long
        val score = resolver.resolve(typeOf<Score>(), defaultTestChain) as Score
        assertIs<Score>(score)
    }

    @Test
    fun `ValueClassResolver caches constructor information per value class`() {
        val resolver = ValueClassResolver()

        resolver.resolve(typeOf<UserId>(), defaultTestChain)
        resolver.resolve(typeOf<UserId>(), defaultTestChain)

        assertEquals(1, resolver.cachedValueClassCount)
    }

    @Test
    fun `ValueClassResolver keeps distinct value classes independent`() {
        val resolver = ValueClassResolver()

        resolver.resolve(typeOf<UserId>(), defaultTestChain)
        resolver.resolve(typeOf<Email>(), defaultTestChain)

        assertEquals(2, resolver.cachedValueClassCount)
    }

    @Test
    fun `ValueClassResolver cache is safe when shared across threads`() {
        val resolver = ValueClassResolver()
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable {
                    resolver.resolve(typeOf<UserId>(), ResolverChain(defaultTestChain.resolvers, null))
                }
            }
            val results = executor.invokeAll(tasks).map { it.get() }

            assertTrue(results.all { it is UserId })
            assertEquals(1, resolver.cachedValueClassCount)
        } finally {
            executor.shutdownNow()
        }
    }
}
