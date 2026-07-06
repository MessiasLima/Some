package dev.appoutlet.some.android.resolver

import android.graphics.Color
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [Color] values when the runtime API level supports [Color.valueOf].
 *
 * @param random Random source used when generating RGB channel values.
 */
class ColorResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Color>()

    override fun resolve(type: KType, chain: ResolverChain): Color {
        return Color.valueOf(
            random.nextFloat(),
            random.nextFloat(),
            random.nextFloat(),
        )
    }
}
