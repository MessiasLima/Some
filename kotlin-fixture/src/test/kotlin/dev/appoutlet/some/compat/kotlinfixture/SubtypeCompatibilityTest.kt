package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertIs

class SubtypeCompatibilityTest {

    @Test
    fun `subType maps superclass or interface to concrete subtype`() {
        val fixture = kotlinFixture {
            subType<Animal, Dog>()
        }

        val animal: Animal = fixture()
        assertIs<Dog>(animal)
    }

    @Test
    fun `subType preserves derived configuration overrides`() {
        val base = kotlinFixture {
            subType<Animal, Dog>()
        }

        val derived = base.new {
            subType<Animal, Cat>()
        }

        assertIs<Dog>(base<Animal>())
        assertIs<Cat>(derived<Animal>())
    }

    @Test
    fun `subType mapping Number to Int generates Int instance`() {
        val fixture = kotlinFixture {
            subType<Number, Int>()
        }

        val number: Number = fixture()
        assertIs<Int>(number)
    }
}
