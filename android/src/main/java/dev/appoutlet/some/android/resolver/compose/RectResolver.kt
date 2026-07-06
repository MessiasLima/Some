package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Rect
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1000f

/**
 * Resolves [Rect] types with randomized float bounds.
 *
 * Generated rectangles always satisfy `left < right` and `top < bottom`.
 *
 * @param random Random source used when generating coordinates.
 */
class RectResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Rect>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val left = (random.nextFloat() * 2 - 1) * COORDINATE_BOUND
        val top = (random.nextFloat() * 2 - 1) * COORDINATE_BOUND
        val right = left + random.nextFloat() * COORDINATE_BOUND
        val bottom = top + random.nextFloat() * COORDINATE_BOUND
        return Rect(left, top, right, bottom)
    }
}
