package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.test.defaultTestChain
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NullableResolverTest {
    @Test
    fun `NullableResolver with AlwaysNull strategy returns null`() {
        val resolver = NullableResolver(NullableStrategy.AlwaysNull, Random.Default)
        
        repeat(1000) {
            val result = resolver.resolve(typeOf<String?>(), defaultTestChain)
            assertNull(result)
        }
    }
    
    @Test
    fun `NullableResolver with NeverNull strategy generates non-null values`() {
        val config = SomeConfig().apply {
            nullableStrategy = NullableStrategy.NeverNull
        }
        val resolvers = config.buildResolvers()
        
        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }
    
    @Test
    fun `NullableResolver with Random strategy can return null or value`() {
        val config = SomeConfig().apply {
            nullableStrategy = NullableStrategy.Random()
        }
        val resolvers = config.buildResolvers()
        
        val results = (1..100).map { ResolverChain(resolvers).resolve(typeOf<String?>()) }
        val hasNull = results.any { it == null }
        val hasValue = results.any { it != null }
        assertTrue(hasNull && hasValue)
    }
    
    @Test
    fun `NullableResolver with Random strategy and probability 0_0 always returns non-null`() {
        val config = SomeConfig().apply {
            nullableStrategy = NullableStrategy.Random(probability = 0.0)
        }
        val resolvers = config.buildResolvers()
        
        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNotNull(result)
            assertIs<String>(result)
        }
    }
    
    @Test
    fun `NullableResolver with Random strategy and probability 1_0 always returns null`() {
        val config = SomeConfig().apply {
            nullableStrategy = NullableStrategy.Random(probability = 1.0)
        }
        val resolvers = config.buildResolvers()
        
        repeat(1000) {
            val result = ResolverChain(resolvers).resolve(typeOf<String?>())
            assertNull(result)
        }
    }
    
    @Test
    fun `NullableResolver canResolve detects nullable types`() {
        val resolver = NullableResolver(NullableStrategy.Random(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<String?>()))
    }
    
    @Test
    fun `NullableResolver rejects non-nullable types`() {
        val resolver = NullableResolver(NullableStrategy.Random(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
    }
}
