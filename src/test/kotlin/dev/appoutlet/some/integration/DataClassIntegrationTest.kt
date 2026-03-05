package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

sealed class TestSealed {
    data class A(val x: Int) : TestSealed()
    data class B(val y: String) : TestSealed()
    object C : TestSealed()
}

sealed interface TestSealedInterface {
    data class X(val value: Int) : TestSealedInterface
    data class Y(val name: String) : TestSealedInterface
    data class Z(val flag: Boolean) : TestSealedInterface
}

class DataClassIntegrationTest {
    @Test
    fun `simple data class with primitives`() {
        data class Address(val street: String, val city: String)
        data class Person(val name: String, val age: Int, val address: Address)
        
        val person: Person = some<Person>()
        assertTrue(person.name.isNotEmpty())
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
        val result = some<TestSealed>()
        assertIs<TestSealed>(result)
    }
    
    @Test
    fun `sealed interface hierarchy`() {
        val result = some<TestSealedInterface>()
        assertIs<TestSealedInterface>(result)
    }
}
