package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Offset
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1000f

/**
 * Resolves [Offset] types with randomized float coordinates.
 *
 * @param random Random source used when generating coordinates.
 */
class OffsetResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Offset>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val x = (random.nextFloat() * 2 - 1) * COORDINATE_BOUND
        val y = (random.nextFloat() * 2 - 1) * COORDINATE_BOUND
        return Offset(x, y)
    }
}
