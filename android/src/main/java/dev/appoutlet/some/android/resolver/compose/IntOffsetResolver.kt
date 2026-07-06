package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.IntOffset
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val COORDINATE_BOUND = 1000

/**
 * Resolves [IntOffset] types with randomized integer coordinates.
 *
 * @param random Random source used when generating coordinates.
 */
class IntOffsetResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<IntOffset>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val x = random.nextInt(-COORDINATE_BOUND, COORDINATE_BOUND)
        val y = random.nextInt(-COORDINATE_BOUND, COORDINATE_BOUND)
        return IntOffset(x, y)
    }
}
