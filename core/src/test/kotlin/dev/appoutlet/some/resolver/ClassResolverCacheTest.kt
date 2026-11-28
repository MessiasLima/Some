package dev.appoutlet.some.resolver

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ClassResolverCacheTest {
    @Test
    fun `constructorsFor reuses cached constructors for the same class`() {
        val lookups = AtomicInteger()
        val cache = ClassResolverCache { kClass: KClass<*> ->
            lookups.incrementAndGet()
            kClass.constructors.toList()
        }

        val first = cache.constructorsFor(SimpleDataClass::class)
        val second = cache.constructorsFor(SimpleDataClass::class)

        assertSame(first, second)
        assertEquals(1, lookups.get())
        assertEquals(1, cache.size)
    }

    @Test
    fun `constructorsFor keeps distinct classes independent`() {
        val lookups = AtomicInteger()
        val cache = ClassResolverCache { kClass: KClass<*> ->
            lookups.incrementAndGet()
            kClass.constructors.toList()
        }

        cache.constructorsFor(SimpleDataClass::class)
        cache.constructorsFor(EmptyConstructorClass::class)
        cache.constructorsFor(SimpleDataClass::class)

        assertEquals(2, lookups.get())
        assertEquals(2, cache.size)
    }

    @Test
    fun `constructorsFor preserves constructor declaration order`() {
        val cache = ClassResolverCache()

        val constructors = cache.constructorsFor(FallbackClass::class)

        assertEquals(FallbackClass::class.constructors.toList(), constructors)
    }

    @Test
    fun `constructorsFor is safe when shared across threads`() {
        val lookups = AtomicInteger()
        val cache = ClassResolverCache { kClass: KClass<*> ->
            lookups.incrementAndGet()
            kClass.constructors.toList()
        }
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable { cache.constructorsFor(SimpleDataClass::class) }
            }
            val results: List<List<KFunction<*>>> = executor.invokeAll(tasks).map { it.get() }

            assertTrue(results.all { it === results.first() })
            assertEquals(1, lookups.get())
            assertEquals(1, cache.size)
        } finally {
            executor.shutdownNow()
        }
    }
}
