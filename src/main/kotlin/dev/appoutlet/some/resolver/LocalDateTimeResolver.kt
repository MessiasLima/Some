package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import java.time.LocalDateTime

class LocalDateTimeResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("LocalDateTime")
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val dayOfYear = random.nextInt(1, 366)
        val hour = random.nextInt(24)
        val minute = random.nextInt(60)
        return LocalDateTime.of(2024, 1, dayOfYear, hour, minute)
    }
}
