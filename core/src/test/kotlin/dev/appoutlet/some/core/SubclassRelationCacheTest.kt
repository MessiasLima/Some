package dev.appoutlet.some.core

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubclassRelationCacheTest {
    @Test
    fun `isSubclassOf returns the same result as a direct subclass check`() {
        val cache = SubclassRelationCache()

        assertTrue(cache.isSubclassOf(String::class, CharSequence::class))
        assertTrue(cache.isSubclassOf(MutableList::class, List::class))
        assertTrue(cache.isSubclassOf(Int::class, Number::class))
        assertTrue(cache.isSubclassOf(String::class, String::class))
        assertTrue(cache.isSubclassOf(String::class, Any::class))
        assertFalse(cache.isSubclassOf(List::class, Set::class))
        assertFalse(cache.isSubclassOf(String::class, Int::class))
    }

    @Test
    fun `isSubclassOf reuses cached result for the same pair`() {
        val checks = mutableListOf<Pair<KClass<*>, KClass<*>>>()
        val cache = SubclassRelationCache { subclass, superclass ->
            checks += subclass to superclass
            subclass == String::class && superclass == CharSequence::class
        }

        repeat(5) {
            assertTrue(cache.isSubclassOf(String::class, CharSequence::class))
        }

        assertEquals(1, checks.size)
        assertEquals(1, cache.size)
    }

    @Test
    fun `isSubclassOf keeps distinct pairs independent`() {
        val checks = mutableListOf<Pair<KClass<*>, KClass<*>>>()
        val cache = SubclassRelationCache { subclass, superclass ->
            checks += subclass to superclass
            true
        }

        cache.isSubclassOf(String::class, CharSequence::class)
        cache.isSubclassOf(Int::class, Number::class)
        cache.isSubclassOf(String::class, CharSequence::class)
        cache.isSubclassOf(Int::class, Number::class)

        assertEquals(2, checks.size)
        assertEquals(2, cache.size)
    }

    @Test
    fun `isSubclassOf is safe when shared across threads`() {
        val checks = java.util.concurrent.atomic.AtomicInteger()
        val cache = SubclassRelationCache { subclass, superclass ->
            checks.incrementAndGet()
            subclass == String::class && superclass == CharSequence::class
        }
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable { cache.isSubclassOf(String::class, CharSequence::class) }
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
    fun `isSubclassOfCached delegates to the shared cache`() {
        assertTrue(String::class.isSubclassOfCached(CharSequence::class))
        assertTrue(MutableList::class.isSubclassOfCached(List::class))
        assertFalse(List::class.isSubclassOfCached(Set::class))
    }
}
