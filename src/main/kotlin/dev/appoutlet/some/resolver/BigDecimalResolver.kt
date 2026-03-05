package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import java.math.BigDecimal

class BigDecimalResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<BigDecimal>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return BigDecimal(random.nextLong())
    }
}
