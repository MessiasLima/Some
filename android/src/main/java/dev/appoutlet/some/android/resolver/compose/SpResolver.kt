package dev.appoutlet.some.android.resolver.compose

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val MAX_SP = 100f

/**
 * Resolves [TextUnit] types with randomized float values in `sp`.
 *
 * @param random Random source used when generating values.
 */
class SpResolver(
    private val random: Random
) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<TextUnit>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return (random.nextFloat() * MAX_SP).sp
    }
}
