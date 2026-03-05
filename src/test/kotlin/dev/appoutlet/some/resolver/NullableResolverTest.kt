package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NullableResolverTest {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `NullableResolver with AlwaysNull strategy returns null`() {
        val resolver = NullableResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            nullableStrategy = NullableStrategy.AlwaysNull
        })
        
        val result = resolver.resolve(typeOf<String?>(), context, chain)
        assertNull(result)
    }
    
    @Test
    fun `NullableResolver with NeverNull strategy generates non-null values`() {
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            nullableStrategy = NullableStrategy.NeverNull
        })
        
        val result = chain.resolve(typeOf<String?>(), context)
        assertNotNull(result)
        assertIs<String>(result)
    }
    
    @Test
    fun `NullableResolver with Random strategy can return null or value`() {
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            nullableStrategy = NullableStrategy.Random
        })
        
        val results = (1..100).map { chain.resolve(typeOf<String?>(), context) }
        val hasNull = results.any { it == null }
        val hasValue = results.any { it != null }
        assertTrue(hasNull && hasValue)
    }
    
    @Test
    fun `NullableResolver canResolve detects nullable types`() {
        val resolver = NullableResolver()
        assertTrue(resolver.canResolve(typeOf<String?>()))
    }
    
    @Test
    fun `NullableResolver rejects non-nullable types`() {
        val resolver = NullableResolver()
        assertTrue(!resolver.canResolve(typeOf<String>()))
    }
}
