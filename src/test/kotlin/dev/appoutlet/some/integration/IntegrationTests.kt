package dev.appoutlet.some.integration

import dev.appoutlet.some.SomeUnresolvableTypeException
import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.StringStrategy
import dev.appoutlet.some.some
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

data class Address(val street: String, val city: String)
data class Person(val name: String, val age: Int, val address: Address)
sealed class PaymentMethod {
    data class Card(val last4: String) : PaymentMethod()
    data class BankTransfer(val iban: String) : PaymentMethod()
    object Cash : PaymentMethod()
}

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
        val customSome = some {
            register(String::class) { "custom-value" }
        }
        
        data class Simple(val text: String)
        val result: Simple = customSome.some<Simple>()
        assertEquals("custom-value", result.text)
    }
    
    @Test
    fun `seeded random produces identical output across runs`() {
        data class SimpleData(val id: Int, val name: String)
        
        val some1 = some { seed = 12345L }
        val some2 = some { seed = 12345L }
        
        val result1: SimpleData = some1.some<SimpleData>()
        val result2: SimpleData = some2.some<SimpleData>()
        
        assertEquals(result1.id, result2.id)
        assertEquals(result1.name, result2.name)
    }
    
    @Test
    fun `nullable strategy NeverNull produces no nulls in 1000 runs`() {
        data class WithNullable(val required: String, val optional: String?)
        
        val someWithConfig = some {
            nullableStrategy = NullableStrategy.NeverNull
        }
        
        repeat(1000) {
            val result: WithNullable = someWithConfig.some<WithNullable>()
            assertTrue(result.required.isNotEmpty())
            assertTrue(result.optional != null)
        }
    }
    
    @Test
    fun `nullable strategy AlwaysNull produces all nulls`() {
        data class WithNullable(val optional: String?)
        
        val someWithConfig = some {
            nullableStrategy = NullableStrategy.AlwaysNull
        }
        
        repeat(100) {
            val result: WithNullable = someWithConfig.some<WithNullable>()
            assertTrue(result.optional == null)
        }
    }
    
    @Test
    fun `collection strategy size range is respected`() {
        val someWithConfig = some {
            collectionStrategy = CollectionStrategy(5..10)
        }
        
        val listResult: List<String> = someWithConfig.some<List<String>>()
        assertTrue(listResult.size in 5..10)
        
        val mapResult: Map<String, String> = someWithConfig.some<Map<String, String>>()
        assertTrue(mapResult.size in 5..10)
    }
    
    @Test
    fun `string strategy Uuid produces valid UUIDs`() {
        val someWithConfig = some {
            stringStrategy = StringStrategy.Uuid
        }
        
        repeat(10) {
            val result: String = someWithConfig.some<String>()
            assertTrue(result.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
        }
    }
    
    @Test
    fun `different runs produce different values`() {
        val result1: Int = some<Int>()
        val result2: Int = some<Int>()
        assertNotEquals(result1, result2)
    }
}
