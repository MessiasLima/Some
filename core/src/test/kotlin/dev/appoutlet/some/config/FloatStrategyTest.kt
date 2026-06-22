package dev.appoutlet.some.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class FloatStrategyTest {
    @Test
    fun `FloatStrategy has default range 0_0 to 1_0`() {
        val strategy = FloatStrategy()
        assertEquals(0.0f, strategy.range.start)
        assertEquals(1.0f, strategy.range.endInclusive)
    }

    @Test
    fun `FloatStrategy default companion is no-arg instance`() {
        val strategy = FloatStrategy.default
        assertEquals(0.0f, strategy.range.start)
        assertEquals(1.0f, strategy.range.endInclusive)
    }

    @Test
    fun `FloatStrategy accepts valid range`() {
        val strategy = FloatStrategy(1.0f..5.0f)
        assertEquals(1.0f, strategy.range.start)
        assertEquals(5.0f, strategy.range.endInclusive)
    }

    @Test
    fun `FloatStrategy accepts zero-width range`() {
        val strategy = FloatStrategy(3.0f..3.0f)
        assertEquals(3.0f, strategy.range.start)
        assertEquals(3.0f, strategy.range.endInclusive)
    }

    @Test
    fun `FloatStrategy accepts negative range`() {
        val strategy = FloatStrategy(-5.0f..-1.0f)
        assertEquals(-5.0f, strategy.range.start)
        assertEquals(-1.0f, strategy.range.endInclusive)
    }

    @Test
    fun `FloatStrategy rejects inverted range`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            FloatStrategy(5.0f..2.0f)
        }
        assertEquals(
            "range.start must be less than or equal to range.endInclusive",
            exception.message
        )
    }

    @Test
    fun `FloatStrategy fixed constructor is equivalent to zero-width range`() {
        val fixed = FloatStrategy(3.0f)
        val range = FloatStrategy(3.0f..3.0f)
        assertEquals(range.range.start, fixed.range.start)
        assertEquals(range.range.endInclusive, fixed.range.endInclusive)
    }

    @Test
    fun `FloatStrategy key is its own class`() {
        assertSame(FloatStrategy::class, FloatStrategy().key)
    }
}
