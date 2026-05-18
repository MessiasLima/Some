package dev.appoutlet.some.integration

import dev.appoutlet.some.some
import dev.appoutlet.some.someSetup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PropertyFactoryIntegrationTest {

    data class User(
        val id: Int,
        val name: String,
        val role: String,
        val age: Int = 25,
        val city: String = "Lisbon"
    )

    data class User2(
        val id: Int,
        val name: String,
        val role: String,
        val age: Int = 25,
        val city: String = "Lisbon"
    )

    @Test
    fun `should override single property in data class`() {
        val user = some<User> {
            property(User::name) { "Alice" }
        }

        assertEquals("Alice", user.name)
        // Age should be auto-generated (non-zero/non-null)
        assertNotEquals(0, user.age)
    }

    @Test
    fun `should override multiple properties in data class`() {
        val user = some<User> {
            property(User::name) { "Bob" }
            property(User::role) { "Admin" }
        }

        assertEquals("Bob", user.name)
        assertEquals("Admin", user.role)
        assertNotEquals(0, user.id)
    }

    @Test
    fun `should use property factory from someSetup`() {
        val some = someSetup {
            property(User::name) { "Charlie" }
        }

        val user = some<User>()

        assertEquals("Charlie", user.name)
    }

    @Test
    fun `type factory should take precedence over property factory`() {
        val user = some<User> {
            register(User::class) {
                User(
                    id = 123,
                    name = "TypeFactory",
                    role = "TypeFactory role"
                )
            }

            property(User::name) { "PropertyFactory" }
        }

        assertEquals("TypeFactory", user.name)
    }

    @Test
    fun `should silently ignore property factory for non-existent property`() {
        // This is tricky to test with KProperty1 since it's type-safe.
        // However, we can simulate what happens if a property factory is registered for another class
        // but somehow ends up in the map for this class, or just ensure it doesn't crash.

        val user = some<User> {
            property(User2::name) { "Should be ignored" }
        }

        assertNotEquals("Should be ignored", user.name)
    }

    @Test
    fun `one-off overrides should not mutate default configuration`() {
        some<User> {
            property(User::name) { "Temporary" }
        }

        val user = some<User>()
        assertNotEquals("Temporary", user.name)
    }

    @Test
    fun `should override optional parameter and keep other defaults`() {
        val user = some<User> {
            property(User::age) { 30 }
        }

        assertEquals(30, user.age)
        assertEquals("Lisbon", user.city) // Default kept
        assertNotEquals("", user.name) // Auto-generated
    }

    @Test
    fun `should override all optional parameters`() {
        val user = some<User> {
            property(User::age) { 40 }
            property(User::city) { "Porto" }
        }

        assertEquals(40, user.age)
        assertEquals("Porto", user.city)
    }
}
