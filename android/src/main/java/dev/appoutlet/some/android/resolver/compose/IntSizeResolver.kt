package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.IntSize
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MAX_DIMENSION = 1000

/**
 * Resolves [IntSize] types with randomized positive integer dimensions.
 *
 * @param random Random source used when generating dimensions.
 */
class IntSizeResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<IntSize>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val width = random.nextInt(1, MAX_DIMENSION)
        val height = random.nextInt(1, MAX_DIMENSION)
        return IntSize(width, height)
    }
}
