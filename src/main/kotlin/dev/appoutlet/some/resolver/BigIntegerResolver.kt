package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import java.math.BigInteger

class BigIntegerResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("BigInteger")
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return BigInteger.valueOf(random.nextLong())
    }
}
