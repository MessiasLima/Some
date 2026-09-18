package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SequenceStrategyTest {

    @Test
    fun `Unbounded strategy creates infinite sequence`() {
        val fixture = kotlinFixture()

        val items = fixture.asSequence<Int>(SequenceStrategy.Unbounded).take(10).toList()

        assertEquals(10, items.size)
    }

    @Test
    fun `Bounded 0 produces empty sequence`() {
        val fixture = kotlinFixture()

        val items = fixture.asSequence<Int>(SequenceStrategy.Bounded(0)).toList()

        assertTrue(items.isEmpty())
    }

    @Test
    fun `Bounded N produces exactly N elements`() {
        val fixture = kotlinFixture()

        val items = fixture.asSequence<String>(SequenceStrategy.Bounded(5)).toList()

        assertEquals(5, items.size)
    }

    @Test
    fun `Bounded throws for negative element count`() {
        assertFailsWith<IllegalArgumentException> {
            SequenceStrategy.Bounded(-1)
        }
    }

    @Test
    fun `filters are applied before bounded limit`() {
        val fixture = kotlinFixture {
            filter<Int> {
                filter { it % 2 == 0 }
            }
        }

        val items = fixture.asSequence<Int>(SequenceStrategy.Bounded(5)).toList()

        assertEquals(5, items.size)
        assertTrue(items.all { it % 2 == 0 })
    }
}
