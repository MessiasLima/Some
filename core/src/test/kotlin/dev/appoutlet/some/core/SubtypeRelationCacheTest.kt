package dev.appoutlet.some.core

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubtypeRelationCacheTest {
    @Test
    fun `isSubtypeOf returns the same result as a direct subtype check`() {
        val cache = SubtypeRelationCache()

        assertTrue(cache.isSubtypeOf(typeOf<MutableList<String>>(), typeOf<List<*>>()))
        assertTrue(cache.isSubtypeOf(typeOf<List<String>>(), typeOf<List<Any>>()))
        assertTrue(cache.isSubtypeOf(typeOf<String>(), typeOf<String?>()))
        assertTrue(cache.isSubtypeOf(typeOf<List<*>>(), typeOf<List<*>>()))
        assertFalse(cache.isSubtypeOf(typeOf<List<String>>(), typeOf<MutableList<*>>()))
        assertFalse(cache.isSubtypeOf(typeOf<String?>(), typeOf<String>()))
        assertFalse(cache.isSubtypeOf(typeOf<List<Any>>(), typeOf<List<String>>()))
    }

    @Test
    fun `isSubtypeOf reuses cached result for the same pair`() {
        val checks = mutableListOf<Pair<KType, KType>>()
        val cache = SubtypeRelationCache { value, base ->
            checks += value to base
            value == typeOf<String>() && base == typeOf<CharSequence>()
        }

        repeat(5) {
            assertTrue(cache.isSubtypeOf(typeOf<String>(), typeOf<CharSequence>()))
        }

        assertEquals(1, checks.size)
        assertEquals(1, cache.size)
    }

    @Test
    fun `isSubtypeOf keeps distinct pairs independent`() {
        val checks = mutableListOf<Pair<KType, KType>>()
        val cache = SubtypeRelationCache { value, base ->
            checks += value to base
            true
        }

        cache.isSubtypeOf(typeOf<String>(), typeOf<CharSequence>())
        cache.isSubtypeOf(typeOf<List<String>>(), typeOf<List<*>>())
        cache.isSubtypeOf(typeOf<String>(), typeOf<CharSequence>())
        cache.isSubtypeOf(typeOf<List<String>>(), typeOf<List<*>>())

        assertEquals(2, checks.size)
        assertEquals(2, cache.size)
    }

    @Test
    fun `isSubtypeOf is safe when shared across threads`() {
        val checks = java.util.concurrent.atomic.AtomicInteger()
        val cache = SubtypeRelationCache { value, base ->
            checks.incrementAndGet()
            value == typeOf<String>() && base == typeOf<CharSequence>()
        }
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable { cache.isSubtypeOf(typeOf<String>(), typeOf<CharSequence>()) }
            }
            val results = executor.invokeAll(tasks).map { it.get() }

            assertTrue(results.all { it })
            assertEquals(1, checks.get())
            assertEquals(1, cache.size)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `isSubtypeOfCached delegates to the shared cache`() {
        assertTrue(typeOf<MutableList<String>>().isSubtypeOfCached(typeOf<List<*>>()))
        assertTrue(typeOf<String>().isSubtypeOfCached(typeOf<String?>()))
        assertFalse(typeOf<List<String>>().isSubtypeOfCached(typeOf<MutableList<*>>()))
    }
}
