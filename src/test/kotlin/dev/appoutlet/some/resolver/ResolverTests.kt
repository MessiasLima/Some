package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.core.FixtureContext
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

enum class TestColor { RED, GREEN, BLUE }

sealed class TestPaymentMethod {
    data class Card(val last4: String) : TestPaymentMethod()
    object Cash : TestPaymentMethod()
}

object TestSingleton {
    val value = 42
}

class ResolverTests {
    private val config = SomeConfig()
    private val chain = config.buildChain()
    
    @Test
    fun `NullableResolver handles nullable types`() {
        val resolver = NullableResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            nullableStrategy = NullableStrategy.AlwaysNull
        })
        
        val result = resolver.resolve(typeOf<String?>(), context, chain)
        assertEquals(result, null)
    }
    
    @Test
    fun `NullableResolver with NeverNull strategy generates non-null values`() {
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            nullableStrategy = NullableStrategy.NeverNull
        })
        
        val result = chain.resolve(typeOf<String?>(), context)
        assertTrue(result != null)
        assertIs<String>(result)
    }
    
    @Test
    fun `EnumResolver picks random enum value`() {
        val resolver = EnumResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<TestColor>(), context, chain)
        assertIs<TestColor>(result)
    }
    
    @Test
    fun `ObjectResolver returns singleton`() {
        val resolver = ObjectResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<TestSingleton>(), context, chain)
        assertSame(result, TestSingleton)
    }
    
    @Test
    fun `SealedClassResolver picks random subclass`() {
        val resolver = SealedClassResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<TestPaymentMethod>(), context, chain)
        assertTrue(result is TestPaymentMethod)
    }
    
    @Test
    fun `StringResolver generates random string`() {
        val resolver = StringResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            stringStrategy = StringStrategy.Random
        })
        
        val result = resolver.resolve(typeOf<String>(), context, chain)
        assertIs<String>(result)
        assertTrue(result.length == 8)
    }
    
    @Test
    fun `StringResolver generates UUID`() {
        val resolver = StringResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            stringStrategy = StringStrategy.Uuid
        })
        
        val result = resolver.resolve(typeOf<String>(), context, chain)
        assertIs<String>(result)
        assertTrue((result as String).contains("-"))
    }
    
    @Test
    fun `IntResolver generates int values`() {
        val resolver = IntResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<Int>(), context, chain)
        assertIs<Int>(result)
    }
    
    @Test
    fun `LongResolver generates long values`() {
        val resolver = LongResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<Long>(), context, chain)
        assertIs<Long>(result)
    }
    
    @Test
    fun `BooleanResolver generates boolean values`() {
        val resolver = BooleanResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig())
        
        val result = resolver.resolve(typeOf<Boolean>(), context, chain)
        assertIs<Boolean>(result)
    }
    
    @Test
    fun `ListResolver generates list with correct size`() {
        val resolver = ListResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            collectionStrategy = CollectionStrategy(3..5)
        })
        
        val result = resolver.resolve(typeOf<List<String>>(), context, chain)
        assertIs<List<*>>(result)
        assertTrue((result as List<*>).size in 3..5)
    }
    
    @Test
    fun `MapResolver generates map with correct size`() {
        val resolver = MapResolver()
        val context = FixtureContext(Random.Default, emptyList(), SomeConfig().apply {
            collectionStrategy = CollectionStrategy(2..4)
        })
        
        val result = resolver.resolve(typeOf<Map<String, Int>>(), context, chain)
        assertIs<Map<*, *>>(result)
        assertTrue((result as Map<*, *>).size in 2..4)
    }
}
