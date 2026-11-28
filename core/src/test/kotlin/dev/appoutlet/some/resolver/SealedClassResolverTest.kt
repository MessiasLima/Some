package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

sealed class TestPaymentMethod {
    data class Card(val last4: String) : TestPaymentMethod()
    data object Cash : TestPaymentMethod()
}

sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data class Success(val paymentMethod: TestPaymentMethod) : UiState
    data class Failure(val message: String) : UiState
}

sealed class NestedSealed {
    sealed class Middle : NestedSealed() {
        data class Leaf(val value: Int) : Middle()
        data object LeafObject : Middle()
    }

    data class Other(val value: String) : NestedSealed()
}

class SealedClassResolverTest {
    @Test
    fun `SealedClassResolver picks random subclass`() {
        val resolver = SealedClassResolver(Random.Default)

        val result = resolver.resolve(typeOf<TestPaymentMethod>(), defaultTestChain)
        assertTrue(result is TestPaymentMethod)
    }

    @Test
    fun `SealedClassResolver resolves sealed interfaces`() {
        val resolver = SealedClassResolver(Random.Default)

        val result = resolver.resolve(typeOf<UiState>(), defaultTestChain)
        assertTrue(result is UiState)
    }

    @Test
    fun `SealedClassResolver canResolve detects sealed types`() {
        val resolver = SealedClassResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<TestPaymentMethod>()))
    }

    @Test
    fun `SealedClassResolver rejects non-sealed types`() {
        val resolver = SealedClassResolver(Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
    }

    @Test
    fun `SealedClassResolver caches leaf subclasses per sealed class`() {
        val resolver = SealedClassResolver(Random.Default)

        resolver.resolve(typeOf<TestPaymentMethod>(), defaultTestChain)
        resolver.resolve(typeOf<TestPaymentMethod>(), defaultTestChain)

        assertEquals(1, resolver.cachedSealedCount)
    }

    @Test
    fun `SealedClassResolver keeps distinct sealed caches independent`() {
        val resolver = SealedClassResolver(Random.Default)

        resolver.resolve(typeOf<TestPaymentMethod>(), defaultTestChain)
        resolver.resolve(typeOf<UiState>(), defaultTestChain)

        assertEquals(2, resolver.cachedSealedCount)
    }

    @Test
    fun `SealedClassResolver resolves nested hierarchies to leaf types`() {
        val resolver = SealedClassResolver(Random.Default)

        val results = List(100) {
            resolver.resolve(typeOf<NestedSealed>(), defaultTestChain)
        }

        assertTrue(results.all { it is NestedSealed })
        assertTrue(results.none { it != null && it::class == NestedSealed.Middle::class })
        assertTrue(results.any { it is NestedSealed.Middle.Leaf })
        assertTrue(results.any { it is NestedSealed.Middle.LeafObject })
        assertTrue(results.any { it is NestedSealed.Other })
    }

    @Test
    fun `SealedClassResolver cache is safe when shared across threads`() {
        val resolver = SealedClassResolver(Random.Default)
        val executor = Executors.newFixedThreadPool(4)

        try {
            val tasks = List(32) {
                Callable {
                    resolver.resolve(typeOf<UiState>(), ResolverChain(defaultTestChain.resolvers, null))
                }
            }
            val results = executor.invokeAll(tasks).map { it.get() }

            assertTrue(results.all { it is UiState })
            assertEquals(1, resolver.cachedSealedCount)
        } finally {
            executor.shutdownNow()
        }
    }
}
