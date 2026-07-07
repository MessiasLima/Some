package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.Dp
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MAX_DP = 1000f

/**
 * Resolves [Dp] types with randomized float values.
 *
 * @param random Random source used when generating values.
 */
class DpResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Dp>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return Dp(random.nextFloat() * MAX_DP)
    }
}
