package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import kotlin.test.Test
import kotlin.test.assertTrue

class CollectionsIntegrationTest {
    @Test
    fun `list of data classes populated correctly`() {
        data class Person(val name: String, val age: Int)
        
        val users: List<Person> = some<List<Person>>()
        assertTrue(users.isNotEmpty())
        assertTrue(users.all { it.name.isNotEmpty() })
    }
    
    @Test
    fun `map of strings to data classes populated correctly`() {
        data class Person(val name: String, val age: Int)
        
        val userMap: Map<String, Person> = some<Map<String, Person>>()
        assertTrue(userMap.isNotEmpty())
        assertTrue(userMap.values.all { it.name.isNotEmpty() })
    }
}
