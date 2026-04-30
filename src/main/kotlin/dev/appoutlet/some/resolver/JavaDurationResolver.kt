package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.Duration
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class JavaDurationResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Duration>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val seconds = random.nextLong(0, 86400) // max 1 day
        return Duration.ofSeconds(seconds)
    }
}
