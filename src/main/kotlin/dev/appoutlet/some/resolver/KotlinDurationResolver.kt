package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KotlinDurationResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Duration>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val seconds = random.nextLong(0, 86400) // max 1 day
        return seconds.seconds
    }
}
