package dev.appoutlet.some.android.strategy

import androidx.compose.ui.graphics.Color
import dev.appoutlet.some.config.Strategy

/**
 * Strategy for generating Compose [Color] values.
 *
 * Available strategies:
 * - [RandomArgb] - generates fully random red, green, blue, and alpha channels (default)
 * - [RandomHsl] - generates colors from random hue, saturation, and lightness values
 * - [Palette] - chooses from a fixed list of colors
 *
 * Example usage:
 * ```kotlin
 * someSetup {
 *     strategy(ColorStrategy.RandomHsl)
 * }
 * ```
 */
sealed interface ColorStrategy : Strategy {
    override val key get() = ColorStrategy::class

    /**
     * Generates Compose colors with fully random ARGB channels.
     */
    data object RandomArgb : ColorStrategy

    /**
     * Generates Compose colors from random HSL values.
     */
    data object RandomHsl : ColorStrategy

    /**
     * Chooses colors from a fixed palette.
     *
     * @param colors Palette values eligible for generation.
     */
    data class Palette(val colors: List<Color>) : ColorStrategy {
        init {
            require(colors.isNotEmpty()) { "Palette must contain at least one color" }
        }
    }

    companion object {
        /**
         * The default Compose color strategy.
         */
        val default: ColorStrategy get() = RandomArgb
    }
}
