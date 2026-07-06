package dev.appoutlet.some.android.resolver

import android.graphics.Point
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [Point] types with randomized integer coordinates.
 *
 * @param random Random source used when generating coordinates.
 */
class PointResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Point>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return Point(random.nextInt(), random.nextInt())
    }
}
