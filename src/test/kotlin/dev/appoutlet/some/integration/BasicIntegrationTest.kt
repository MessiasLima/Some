package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BasicIntegrationTest {
    @Test
    fun `some generates a data class instance`() {
        data class Person(val name: String, val age: Int)
        val person: Person = some<Person>()
        assertTrue(person.name.isNotEmpty())
        assertTrue(person.age is Int)
    }
    
    @Test
    fun `some generates nested data classes`() {
        data class Address(val street: String, val city: String)
        data class Person(val name: String, val address: Address)
        val person: Person = some<Person>()
        assertTrue(person.name.isNotEmpty())
        assertTrue(person.address.street.isNotEmpty())
        assertTrue(person.address.city.isNotEmpty())
    }
    
    @Test
    fun `some generates a list of data classes`() {
        data class Person(val name: String)
        val persons: List<Person> = some<List<Person>>()
        assertTrue(persons.isNotEmpty())
        assertTrue(persons.all { it.name.isNotEmpty() })
    }
}
