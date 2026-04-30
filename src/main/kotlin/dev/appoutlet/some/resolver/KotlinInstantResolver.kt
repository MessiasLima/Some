package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Instant

class KotlinInstantResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Instant>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val epochSecond = random.nextLong(Instant.DISTANT_PAST.epochSeconds, Instant.DISTANT_FUTURE.epochSeconds)
        return Instant.fromEpochSeconds(epochSecond)
    }
}
