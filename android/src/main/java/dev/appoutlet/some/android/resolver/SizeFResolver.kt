package dev.appoutlet.some.android.resolver

import android.util.SizeF
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val DIMENSION_BOUND = 1_000_000f

/**
 * Resolves [SizeF] types with randomized positive float dimensions.
 *
 * @param random Random source used when generating dimensions.
 */
class SizeFResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<SizeF>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val width = random.nextFloat() * (DIMENSION_BOUND - 1) + 1
        val height = random.nextFloat() * (DIMENSION_BOUND - 1) + 1
        return SizeF(width, height)
    }
}
