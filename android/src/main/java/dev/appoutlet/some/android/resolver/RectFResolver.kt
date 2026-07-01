package dev.appoutlet.some.android.resolver

import android.graphics.RectF
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1_000_000f

/**
 * Resolves [RectF] types with randomized float bounds.
 *
 * Generated rectangles always satisfy `left < right` and `top < bottom`.
 *
 * @param random Random source used when generating coordinates.
 */
class RectFResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<RectF>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val left = random.nextFloat() * (COORDINATE_BOUND * 2) - COORDINATE_BOUND
        val top = random.nextFloat() * (COORDINATE_BOUND * 2) - COORDINATE_BOUND
        val right = left + random.nextFloat() * (COORDINATE_BOUND - 1) + 1
        val bottom = top + random.nextFloat() * (COORDINATE_BOUND - 1) + 1
        return RectF(left, top, right, bottom)
    }
}
