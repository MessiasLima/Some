package dev.appoutlet.some.android.resolver

import android.util.Pair
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType

/**
 * Resolves [Pair] types by delegating both generic type arguments back through the resolver chain.
 *
 * @param random Shared random source accepted for consistency with the resolver provider API.
 */
class PairResolver(
    @Suppress("UNUSED_PARAMETER")
    random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type.classifier == Pair::class

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val firstType = requireNotNull(type.arguments.getOrNull(0)?.type) {
            "Star projection not supported in Pair first type"
        }
        val secondType = requireNotNull(type.arguments.getOrNull(1)?.type) {
            "Star projection not supported in Pair second type"
        }

        return Pair(chain.resolve(firstType), chain.resolve(secondType))
    }
}
