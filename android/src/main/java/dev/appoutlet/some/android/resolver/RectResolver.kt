package dev.appoutlet.some.android.resolver

import android.graphics.Rect
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1_000_000

/**
 * Resolves [Rect] types with randomized integer bounds.
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
        val left = random.nextInt(-COORDINATE_BOUND, COORDINATE_BOUND)
        val top = random.nextInt(-COORDINATE_BOUND, COORDINATE_BOUND)
        val right = left + random.nextInt(1, COORDINATE_BOUND)
        val bottom = top + random.nextInt(1, COORDINATE_BOUND)
        return Rect(left, top, right, bottom)
    }
}
