package dev.appoutlet.some.android.strategy

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorStrategyTest {
    @Test
    fun `ColorStrategy default is RandomArgb`() {
        assertEquals(ColorStrategy.RandomArgb, ColorStrategy.default)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `ColorStrategy Palette rejects empty color lists`() {
        ColorStrategy.Palette(emptyList<Color>())
    }
}
