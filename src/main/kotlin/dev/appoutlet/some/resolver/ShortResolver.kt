package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ShortResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Short>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return random.nextInt().toShort()
    }
}
