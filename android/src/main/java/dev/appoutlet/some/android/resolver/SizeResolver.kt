package dev.appoutlet.some.android.resolver

import android.util.Size
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Resolves [Size] types with randomized positive integer dimensions.
 *
 * @param random Random source used when generating dimensions.
 */
class SizeResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Size>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return Size(random.nextInt(1, Int.MAX_VALUE), random.nextInt(1, Int.MAX_VALUE))
    }
}
