package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.Resolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class LongResolver(val random: Random) : Resolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Long>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return random.nextLong()
    }
}
