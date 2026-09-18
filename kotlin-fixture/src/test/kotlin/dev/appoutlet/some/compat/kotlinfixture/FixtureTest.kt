package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FixtureTest {

    @Test
    fun `fixture generates primitive and data class values`() {
        val fixture = kotlinFixture()

        val number: Int = fixture()
        val text: String = fixture()
        val user: TestUser = fixture()

        assertNotNull(number)
        assertNotNull(text)
        assertNotNull(user)
        assertNotNull(user.name)
    }

    @Test
    fun `fixture selects values from a non-empty range`() {
        val fixture = kotlinFixture()
        val values = listOf("Alice", "Bob", "Charlie")

        val result: String = fixture(values)

        assertTrue(result in values)
    }

    @Test
    fun `fixture generates a value when the range is empty`() {
        val fixture = kotlinFixture()

        val result: String = fixture(emptyList())

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `per-call configuration overrides the fixture configuration only for that call`() {
        val fixture = kotlinFixture {
            factory(String::class) { "default" }
        }

        val overridden: String = fixture {
            factory(String::class) { "override" }
        }

        assertEquals("override", overridden)
        assertEquals("default", fixture())
    }

    @Test
    fun `new creates a fixture using the supplied configuration`() {
        val fixture = kotlinFixture {
            factory(String::class) { "parent" }
        }

        val newFixture = fixture.new {
            factory(Int::class) { 42 }
        }

        assertEquals(42, newFixture<Int>())
        assertEquals("parent", fixture<String>())
    }

    @Test
    fun `create generates a value with one-off configuration`() {
        val fixture = kotlinFixture {
            factory(String::class) { "fixture" }
        }

        val result: Int = fixture.create {
            factory(Int::class) { 42 }
        }

        assertEquals(42, result)
    }
}
