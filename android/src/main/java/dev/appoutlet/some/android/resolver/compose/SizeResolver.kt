package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.geometry.Size
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MAX_DIMENSION = 1000f

/**
 * Resolves [Size] types with randomized positive float dimensions.
 *
 * @param random Random source used when generating dimensions.
 */
class SizeResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Size>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val width = random.nextFloat() * MAX_DIMENSION
        val height = random.nextFloat() * MAX_DIMENSION
        return Size(width, height)
    }
}
