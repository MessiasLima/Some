package dev.appoutlet.some.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloatStrategyTest {
    @Test
    fun `default strategy uses range 0 to 1`() {
        val strategy = FloatStrategy.default
        assertEquals(0.0f..1.0f, strategy.range)
    }

    @Test
    fun `secondary constructor pins value`() {
        val strategy = FloatStrategy(5.0f)
        assertEquals(5.0f..5.0f, strategy.range)
    }

    @Test
    fun `validation rejects inverted range`() {
        assertFailsWith<IllegalArgumentException> {
            FloatStrategy(5.0f..2.0f)
        }
    }

    @Test
    fun `zero-width range is allowed`() {
        val strategy = FloatStrategy(2.0f..2.0f)
        assertEquals(2.0f..2.0f, strategy.range)
    }
}
