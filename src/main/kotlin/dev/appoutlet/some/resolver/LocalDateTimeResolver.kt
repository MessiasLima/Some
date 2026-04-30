package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class LocalDateTimeResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<LocalDateTime>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val dayOfYear = random.nextInt(1, 366)
        val hour = random.nextInt(24)
        val minute = random.nextInt(60)
        val year = (1970..3000).random(random)
        val date = LocalDate.ofYearDay(year, dayOfYear)
        return date.atTime(hour, minute)
    }
}
