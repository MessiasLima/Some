package dev.appoutlet.some.compat.kotlinfixture

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EdgeCasesTest {

    @Test
    fun `strategy parameter validation throws on out of range values`() {
        assertFailsWith<IllegalArgumentException> {
            NullabilityStrategy.RandomlyNullStrategy(-0.1f)
        }
        assertFailsWith<IllegalArgumentException> {
            NullabilityStrategy.RandomlyNullStrategy(1.1f)
        }
        assertFailsWith<IllegalArgumentException> {
            OptionalStrategy.RandomlyOptionalStrategy(-0.1f)
        }
        assertFailsWith<IllegalArgumentException> {
            OptionalStrategy.RandomlyOptionalStrategy(1.1f)
        }
    }

    @Test
    fun `RandomlyOptionalStrategy generates values`() {
        val fixture = kotlinFixture {
            optionalStrategy = OptionalStrategy.RandomlyOptionalStrategy(0.5f)
        }
        val user: TestUser = fixture()
        assertNotNull(user)
    }

    @Test
    fun `ConfigurationBuilder untyped methods store registrations`() {
        val builder = ConfigurationBuilder().apply {
            factory(typeOf<String>()) { "untyped-factory" }
            subType(Animal::class, Dog::class)
            filter<Int>(typeOf<Int>()) {
                filter { it > 0 }
            }
        }
        val config = builder.build()
        val fixture = Fixture(config)

        assertEquals("untyped-factory", fixture<String>())
        assertTrue(fixture<Animal>() is Dog)
        assertTrue(fixture<Int>() > 0)
    }

    @Test
    fun `un-satisfiable filter throws IllegalStateException after max attempts`() {
        val fixture = kotlinFixture {
            filter<Int> {
                filter { false }
            }
        }

        assertFailsWith<IllegalStateException> {
            fixture<Int>()
        }
    }

    @Test
    fun `range selection with filter filters items before picking`() {
        val fixture = kotlinFixture {
            filter<Int> {
                filter { it % 2 == 0 }
            }
        }

        val selected: Int = fixture(listOf(1, 3, 4, 5))
        assertEquals(4, selected)
    }

    @Test
    fun `generic subtype resolution maps type arguments`() {
        val fixture = kotlinFixture {
            subType(List::class, ArrayList::class)
        }

        val list: List<String> = fixture()
        assertNotNull(list)
    }
}
