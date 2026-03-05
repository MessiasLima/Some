package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import java.time.Instant

class InstantResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Instant>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val epochSecond = random.nextLong(0, 4102444800) // 2100-01-01
        return Instant.ofEpochSecond(epochSecond)
    }
}
