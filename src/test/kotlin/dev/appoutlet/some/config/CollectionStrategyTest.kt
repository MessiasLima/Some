package dev.appoutlet.some.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CollectionStrategyTest {
    @Test
    fun `CollectionStrategy accepts valid range`() {
        val strategy = CollectionStrategy(1..5)
        assertEquals(1, strategy.sizeRange.first)
        assertEquals(5, strategy.sizeRange.last)
    }

    @Test
    fun `CollectionStrategy accepts single element range`() {
        val strategy = CollectionStrategy(1..2)
        assertEquals(1, strategy.sizeRange.first)
        assertEquals(2, strategy.sizeRange.last)
    }

    @Test
    fun `CollectionStrategy accepts range starting at 0`() {
        val strategy = CollectionStrategy(0..5)
        assertEquals(0, strategy.sizeRange.first)
        assertEquals(5, strategy.sizeRange.last)
    }

    @Test
    fun `CollectionStrategy rejects negative start range`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CollectionStrategy(-5..5)
        }
        assertEquals("sizeRange.start must be positive", exception.message)
    }

    @Test
    fun `CollectionStrategy rejects range where end equals start`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CollectionStrategy(5..5)
        }
        assertEquals("sizeRange.end must be greater than or equal to sizeRange.start", exception.message)
    }

    @Test
    fun `CollectionStrategy rejects range where end is less than start`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CollectionStrategy(10..5)
        }
        assertEquals("sizeRange.end must be greater than or equal to sizeRange.start", exception.message)
    }

    @Test
    fun `CollectionStrategy rejects negative range`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CollectionStrategy(-10..-5)
        }
        assertEquals("sizeRange.start must be positive", exception.message)
    }

    @Test
    fun `CollectionStrategy with minimum valid range`() {
        val strategy = CollectionStrategy(1..2)
        assertEquals(2, strategy.sizeRange.count())
    }
}
