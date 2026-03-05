package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class DoubleResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Double>()

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return random.nextDouble()
    }
}
