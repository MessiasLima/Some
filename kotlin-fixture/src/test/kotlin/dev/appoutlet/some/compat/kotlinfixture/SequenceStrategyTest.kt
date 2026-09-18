package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SequenceStrategyTest {

    @Test
    fun `unbounded sequence remains lazy`() {
        val fixture = kotlinFixture {
            factory(Int::class) { 7 }
        }

        val values = fixture.asSequence<Int>(SequenceStrategy.Unbounded).take(5).toList()

        assertEquals(listOf(7, 7, 7, 7, 7), values)
    }

    @Test
    fun `bounded sequence emits the requested number of values`() {
        val fixture = kotlinFixture {
            factory(String::class) { "value" }
        }

        val values = fixture.asSequence<String>(SequenceStrategy.Bounded(3)).toList()

        assertEquals(listOf("value", "value", "value"), values)
    }

    @Test
    fun `bounded sequence with zero elements is empty`() {
        val values = kotlinFixture()
            .asSequence<Int>(SequenceStrategy.Bounded(0))
            .toList()

        assertEquals(emptyList(), values)
    }

    @Test
    fun `bounded sequence rejects negative element counts`() {
        assertFailsWith<IllegalArgumentException> {
            SequenceStrategy.Bounded(-1)
        }
    }
}
