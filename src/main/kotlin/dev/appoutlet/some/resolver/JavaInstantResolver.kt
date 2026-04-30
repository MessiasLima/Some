package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.Instant
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class JavaInstantResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Instant>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val epochSecond = random.nextLong(Instant.MIN.epochSecond, Instant.MAX.epochSecond) // 2100-01-01
        return Instant.ofEpochSecond(epochSecond)
    }
}
