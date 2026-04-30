package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.math.BigInteger
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class BigIntegerResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<BigInteger>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return BigInteger.valueOf(random.nextLong())
    }
}
