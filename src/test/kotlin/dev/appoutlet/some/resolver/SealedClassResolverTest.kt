package dev.appoutlet.some.resolver

import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
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
}
