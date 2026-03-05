package dev.appoutlet.some.integration

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

data class Address(val street: String, val city: String)
data class Person(val name: String, val age: Int, val address: Address)

sealed class BaseSealed {
    data class A(val x: Int) : BaseSealed()
    data class B(val y: String) : BaseSealed()
    object C : BaseSealed()
}

class IntegrationTests {
    @Test
    fun `simple data class with primitives`() {
        val person: Person = some<Person>()
        assertTrue(person.name.isNotEmpty())
        assertTrue(person.age is Int)
        assertTrue(person.address.street.isNotEmpty())
    }
    
    @Test
    fun `deeply nested data classes`() {
        data class Level3(val value: String)
        data class Level2(val level3: Level3)
        data class Level1(val level2: Level2)
        
        val result: Level1 = some<Level1>()
        assertTrue(result.level2.level3.value.isNotEmpty())
    }
    
    @Test
    fun `sealed class hierarchy`() {
        val result: BaseSealed = some<BaseSealed>()
        assertTrue(result is BaseSealed)
    }
    
    @Test
    fun `generic wrapper class with various types`() {
        data class Wrapper<T>(val value: T)
        
        val stringWrapper: Wrapper<String> = some<Wrapper<String>>()
        val intWrapper: Wrapper<Int> = some<Wrapper<Int>>()
        
        assertTrue(stringWrapper.value.isNotEmpty())
        assertTrue(intWrapper.value is Int)
    }
    
    @Test
    fun `list of data classes populated correctly`() {
        val users: List<Person> = some<List<Person>>()
        assertTrue(users.isNotEmpty())
        assertTrue(users.all { it.name.isNotEmpty() })
    }
    
    @Test
    fun `map of strings to data classes populated correctly`() {
        val userMap: Map<String, Person> = some<Map<String, Person>>()
        assertTrue(userMap.isNotEmpty())
        assertTrue(userMap.values.all { it.name.isNotEmpty() })
    }
    
    @Test
    fun `custom factory overrides built-in resolver`() {
        val customSome = someSetup {
            register(String::class) { "custom-value" }
        }
        
        data class Simple(val text: String)
        val result: Simple = customSome.some<Simple>()
        assertEquals("custom-value", result.text)
    }
    
    @Test
    fun `seeded random produces identical output across runs`() {
        data class SimpleData(val id: Int, val name: String)
        
        val some1 = someSetup { seed = 12345L }
        val some2 = someSetup { seed = 12345L }
        
        val result1: SimpleData = some1<SimpleData>()
        val result2: SimpleData = some2<SimpleData>()
        
        assertEquals(result1.id, result2.id)
        assertEquals(result1.name, result2.name)
    }
    
    @Test
    fun `nullable strategy NeverNull produces no nulls in 1000 runs`() {
        data class WithNullable(val required: String, val optional: String?)
        
        val someWithConfig = someSetup {
            nullableStrategy = NullableStrategy.NeverNull
        }
        
        repeat(1000) {
            val result: WithNullable = someWithConfig<WithNullable>()
            assertTrue(result.required.isNotEmpty())
            assertTrue(result.optional != null)
        }
    }
    
    @Test
    fun `nullable strategy AlwaysNull produces all nulls`() {
        data class WithNullable(val optional: String?)
        
        val someWithConfig = someSetup {
            nullableStrategy = NullableStrategy.AlwaysNull
        }
        
        repeat(100) {
            val result: WithNullable = someWithConfig<WithNullable>()
            assertEquals(result.optional, null)
        }
    }
    
    @Test
    fun `collection strategy size range is respected`() {
        val someWithConfig = someSetup {
            collectionStrategy = CollectionStrategy(5..10)
        }
        
        val listResult: List<String> = someWithConfig<List<String>>()
        assertTrue(listResult.size in 5..10)
        
        val mapResult: Map<String, String> = someWithConfig<Map<String, String>>()
        assertTrue(mapResult.size in 5..10)
    }
    
    @Test
    fun `string strategy Uuid produces valid UUIDs`() {
        val someWithConfig = someSetup {
            stringStrategy = StringStrategy.Uuid
        }
        
        repeat(10) {
            val result: String = someWithConfig<String>()
            assertTrue(result.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
        }
    }
    
    @Test
    fun `different runs produce different values`() {
        val result1: Int = some<Int>()
        val result2: Int = some<Int>()
        assertNotEquals(result1, result2)
    }
    
    @Test
    fun `aggregated configs does not override base config`() {
        data class TestData(val name: String, val optional: String?)
        
        // Base config: seed for reproducibility, NeverNull strategy
        val baseSome = someSetup {
            seed = 42L
            nullableStrategy = NullableStrategy.NeverNull
        }
        
        // First call with base config - should have non-null optional
        repeat(100) {
            val result1: TestData = baseSome()
            assertTrue(result1.optional != null, "Base config should produce non-null optional")
        }
        
        // Second call with aggregated config - override to AlwaysNull
        // This should NOT mutate the base config
        repeat(100) {
            val result2: TestData = baseSome {
                nullableStrategy = NullableStrategy.AlwaysNull
            }
            assertEquals(null, result2.optional, "Aggregated config should override to null")
        }
        
        // Third call with base config - should still use original base config (NeverNull)
        repeat(100) {
            val result3: TestData = baseSome()
            assertTrue(result3.optional != null, "Base config should NOT be mutated by aggregation")
        }
        
        // Test that we can aggregate again with custom probability
        repeat(100) {
            val result4: TestData = baseSome {
                nullableStrategy = NullableStrategy.Random(probability = 0.0)
            }
            assertTrue(result4.optional != null, "Aggregated config with 0.0 probability should never produce null")
        }
    }
    
    @Test
    fun `aggregated configs can override multiple strategies`() {
        data class ComplexData(
            val id: String,
            val items: List<Int>,
            val optional: String?
        )
        
        // Base config with specific strategies
        val baseSome = someSetup {
            stringStrategy = StringStrategy.Uuid
            collectionStrategy = CollectionStrategy(1..3)
            nullableStrategy = NullableStrategy.NeverNull
        }
        
        val baseResult: ComplexData = baseSome()
        assertTrue(baseResult.id.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
        assertTrue(baseResult.items.size in 1..3)
        assertTrue(baseResult.optional != null)
        
        // Override multiple strategies at once
        val overriddenResult: ComplexData = baseSome {
            stringStrategy = StringStrategy.Random()
            collectionStrategy = CollectionStrategy(10..15)
            nullableStrategy = NullableStrategy.AlwaysNull
        }
        
        // String should not be UUID anymore (very unlikely to match UUID pattern with Random)
        assertTrue(overriddenResult.items.size in 10..15)
        assertEquals(null, overriddenResult.optional)
    }
    
    @Test
    fun `aggregated configs work with custom factories`() {
        data class Product(val name: String, val price: Double)
        
        // Base config with custom factory
        val baseSome = someSetup {
            register(String::class) { "base-string" }
        }
        
        val result1: Product = baseSome()
        assertEquals("base-string", result1.name)
        
        // Aggregate with another custom factory - should override in the aggregated config
        // This should NOT mutate the base config
        val result2: Product = baseSome {
            register(String::class) { "overridden-string" }
        }
        assertEquals("overridden-string", result2.name)
        
        // Base config should NOT be mutated, so it will still use the original factory
        val result3: Product = baseSome()
        assertEquals("base-string", result3.name)
    }
}
