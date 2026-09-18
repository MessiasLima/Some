package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FixtureTest {

    @Test
    fun `fixture generates primitives and data classes`() {
        val fixture = kotlinFixture()

        val intVal: Int = fixture()
        val stringVal: String = fixture()
        val userVal: TestUser = fixture()

        assertNotNull(intVal)
        assertNotNull(stringVal)
        assertNotNull(userVal)
        assertNotNull(userVal.name)
    }

    @Test
    fun `fixture selects value from non-empty iterable`() {
        val fixture = kotlinFixture()

        val item: String = fixture(listOf("A", "B", "C"))
        assertTrue(item in listOf("A", "B", "C"))

        val number: Int = fixture(10..20)
        assertTrue(number in 10..20)
    }

    @Test
    fun `fixture falls back to generated value for empty iterable`() {
        val fixture = kotlinFixture()

        val item: String = fixture(emptyList())
        assertNotNull(item)
        assertTrue(item.isNotBlank())
    }

    @Test
    fun `fixture invoke with per-call configuration does not mutate parent`() {
        val parent = kotlinFixture()

        val overridden: Int = parent {
            factory<Int> { 999 }
        }
        assertEquals(999, overridden)

        val original: Int = parent()
        assertNotEquals(999, original)
    }

    @Test
    fun `new creates derived fixture preserving configuration`() {
        val base = kotlinFixture {
            factory<String> { "base-string" }
        }

        val derived = base.new {
            factory<Int> { 123 }
        }

        assertEquals("base-string", derived<String>())
        assertEquals(123, derived<Int>())

        // Base remains unchanged for Int
        assertEquals("base-string", base<String>())
        assertNotEquals(123, base<Int>())
    }
}
