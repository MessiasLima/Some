package dev.appoutlet.some.android.resolver

import android.graphics.PointF
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1_000_000f

/**
 * Resolves [PointF] types with randomized float coordinates.
 *
 * @param random Random source used when generating coordinates.
 */
class PointFResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<PointF>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val x = random.nextFloat() * (COORDINATE_BOUND * 2) - COORDINATE_BOUND
        val y = random.nextFloat() * (COORDINATE_BOUND * 2) - COORDINATE_BOUND
        return PointF(x, y)
    }
}
