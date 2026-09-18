package dev.appoutlet.some.compat.kotlinfixture

import kotlin.test.Test
import kotlin.test.assertEquals

class FilterCompatibilityTest {

    @Test
    fun `filter predicate filters direct fixture generation`() {
        val fixture = kotlinFixture {
            filter<Int> {
                filter { it % 2 == 0 }
            }
        }

        repeat(10) {
            val val1: Int = fixture()
            assertEquals(0, val1 % 2)
        }
    }

    @Test
    fun `distinct filter generates unique values in sequence`() {
        val fixture = kotlinFixture {
            factory<Int> { range(listOf(1, 1, 2, 2, 3, 3)) }
            filter<Int> {
                distinct()
            }
        }

        val sequence = fixture.asSequence<Int>(SequenceStrategy.Bounded(3)).toList()

        assertEquals(3, sequence.size)
        assertEquals(sequence.size, sequence.toSet().size)
    }
}
