package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.graphics.Color
import dev.appoutlet.some.android.strategy.ColorStrategy
import dev.appoutlet.some.config.DefaultStrategyProvider
import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.core.ResolverChain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.typeOf
import kotlin.test.assertNotNull

class ColorResolverTest {
    @Test
    fun `ColorResolver generates Compose Color values in default RandomArgb mode`() {
        val resolver = ColorResolver(DefaultStrategyProvider(), FloatSequenceRandom(0.1f, 0.2f, 0.3f, 0.4f))

        val result = resolver.resolve(typeOf<Color>(), testChain) as Color

        assertEquals(Color(0.1f, 0.2f, 0.3f, 0.4f), result)
    }

    @Test
    fun `ColorResolver RandomHsl produces HSL converted colors`() {
        val resolver = ColorResolver(
            DefaultStrategyProvider(mapOf(ColorStrategy::class to ColorStrategy.RandomHsl)),
            Random.Default
        )

        val result = resolver.resolve(typeOf<Color>(), testChain) as Color

        assertNotNull(result)
    }

    @Test
    fun `ColorResolver Palette only returns configured colors`() {
        val expected = Color(0.3f, 0.4f, 0.5f, 0.6f)
        val resolver = ColorResolver(
            DefaultStrategyProvider(mapOf(ColorStrategy::class to ColorStrategy.Palette(listOf(expected)))),
            Random.Default
        )

        repeat(10) {
            val result = resolver.resolve(typeOf<Color>(), testChain) as Color
            assertEquals(expected, result)
        }
    }

    @Test
    fun `ColorResolver canResolve detects Compose Color type`() {
        val resolver = ColorResolver(DefaultStrategyProvider(), Random.Default)
        assertTrue(resolver.canResolve(typeOf<Color>()))
    }

    @Test
    fun `ColorResolver rejects non-Compose Color types`() {
        val resolver = ColorResolver(DefaultStrategyProvider(), Random.Default)
        assertFalse(resolver.canResolve(typeOf<String>()))
        assertFalse(resolver.canResolve(typeOf<Int>()))
        assertFalse(resolver.canResolve(typeOf<android.graphics.Color>()))
    }

    private class FloatSequenceRandom(vararg values: Float) : Random() {
        private val values = values.toList()
        private var index = 0

        override fun nextBits(bitCount: Int): Int = 0

        override fun nextFloat(): Float {
            return values.getOrElse(index++) { error("No more float values configured for test random") }
        }
    }

    companion object {
        private val testChain = ResolverChain(emptyList(), NullableStrategy.NullOnCircularReference)
    }
}
