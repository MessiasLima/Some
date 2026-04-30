package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class BigDecimalResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<BigDecimal>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return BigDecimal(random.nextLong())
    }
}
