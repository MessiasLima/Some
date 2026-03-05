package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertTrue

sealed class TestPaymentMethod {
    data class Card(val last4: String) : TestPaymentMethod()
    object Cash : TestPaymentMethod()
}

class SealedClassResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `SealedClassResolver picks random subclass`() {
        val resolver = SealedClassResolver(Random.Default)
        
        val result = resolver.resolve(typeOf<TestPaymentMethod>(), chain)
        assertTrue(result is TestPaymentMethod)
    }
    
    @Test
    fun `SealedClassResolver canResolve detects sealed types`() {
        val resolver = SealedClassResolver(Random.Default)
        assertTrue(resolver.canResolve(typeOf<TestPaymentMethod>()))
    }
    
    @Test
    fun `SealedClassResolver rejects non-sealed types`() {
        val resolver = SealedClassResolver(Random.Default)
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
