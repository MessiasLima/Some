package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
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
        val resolver = SealedClassResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<TestPaymentMethod>(), context, chain)
        assertTrue(result is TestPaymentMethod)
    }
    
    @Test
    fun `SealedClassResolver canResolve detects sealed types`() {
        val resolver = SealedClassResolver()
        assertTrue(resolver.canResolve(typeOf<TestPaymentMethod>()))
    }
    
    @Test
    fun `SealedClassResolver rejects non-sealed types`() {
        val resolver = SealedClassResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
        assertTrue(!resolver.canResolve(typeOf<Int>()))
    }
}
