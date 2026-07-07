package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.graphics.Color
import dev.appoutlet.some.android.strategy.ColorStrategy
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val HUE_MAX = 360f

/**
 * Resolves Compose [Color] values using the active [ColorStrategy].
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used when generating channel values.
 */
class ColorResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val colorStrategy = strategyProvider.get<ColorStrategy>() ?: ColorStrategy.default

    override fun canResolve(type: KType): Boolean = type == typeOf<Color>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return when (colorStrategy) {
            is ColorStrategy.RandomArgb -> resolveRandomArgb()
            is ColorStrategy.RandomHsl -> resolveRandomHsl()
            is ColorStrategy.Palette -> colorStrategy.colors.random(random)
        }
    }

    private fun resolveRandomArgb(): Color {
        return Color(
            red = random.nextFloat(),
            green = random.nextFloat(),
            blue = random.nextFloat(),
            alpha = random.nextFloat()
        )
    }

    private fun resolveRandomHsl(): Color {
        val hue = random.nextFloat() * HUE_MAX
        val saturation = random.nextFloat()
        val lightness = random.nextFloat()
        val alpha = random.nextFloat()

        return Color.hsl(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            alpha = alpha
        )
    }
}
