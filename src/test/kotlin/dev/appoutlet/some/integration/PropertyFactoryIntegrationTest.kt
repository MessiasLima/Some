package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PropertyFactoryIntegrationTest {

    data class SimpleUser(val name: String, val age: Int)

    data class MultiFieldUser(val id: Int, val name: String, val role: String, val active: Boolean)

    data class OptionalUser(val name: String, val age: Int = 25, val city: String = "Lisbon")

    @Test
    fun `should override single property in data class`() {
        val user = some<SimpleUser> {
            property(SimpleUser::name) { "Alice" }
        }

        assertEquals("Alice", user.name)
        // Age should be auto-generated (non-zero/non-null)
        assertNotEquals(0, user.age)
    }

    @Test
    fun `should override multiple properties in data class`() {
        val user = some<MultiFieldUser> {
            property(MultiFieldUser::name) { "Bob" }
            property(MultiFieldUser::role) { "Admin" }
        }

        assertEquals("Bob", user.name)
        assertEquals("Admin", user.role)
        assertNotEquals(0, user.id)
    }

    @Test
    fun `should use property factory from someSetup`() {
        val some = someSetup {
            property(SimpleUser::name) { "Charlie" }
        }

        val user = some<SimpleUser>()

        assertEquals("Charlie", user.name)
    }

    @Test
    fun `type factory should take precedence over property factory`() {
        val user = some<SimpleUser> {
            register(SimpleUser::class) { SimpleUser("TypeFactory", 99) }
            property(SimpleUser::name) { "PropertyFactory" }
        }

        assertEquals("TypeFactory", user.name)
        assertEquals(99, user.age)
    }

    @Test
    fun `should silently ignore property factory for non-existent property`() {
        // This is tricky to test with KProperty1 since it's type-safe.
        // However, we can simulate what happens if a factory is registered for another class
        // but somehow ends up in the map for this class, or just ensure it doesn't crash.

        val user = some<SimpleUser> {
            property(MultiFieldUser::name) { "Should be ignored" }
        }

        assertNotEquals("Should be ignored", user.name)
    }

    @Test
    fun `one-off overrides should not mutate default configuration`() {
        some<SimpleUser> {
            property(SimpleUser::name) { "Temporary" }
        }

        val user = some<SimpleUser>()
        assertNotEquals("Temporary", user.name)
    }

    @Test
    fun `should override optional parameter and keep other defaults`() {
        val user = some<OptionalUser> {
            property(OptionalUser::age) { 30 }
        }

        assertEquals(30, user.age)
        assertEquals("Lisbon", user.city) // Default kept
        assertNotEquals("", user.name) // Auto-generated
    }

    @Test
    fun `should override all optional parameters`() {
        val user = some<OptionalUser> {
            property(OptionalUser::age) { 40 }
            property(OptionalUser::city) { "Porto" }
        }

        assertEquals(40, user.age)
        assertEquals("Porto", user.city)
    }
}
