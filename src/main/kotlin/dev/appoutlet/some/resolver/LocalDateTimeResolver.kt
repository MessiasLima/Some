package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60

class LocalDateTimeResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<LocalDateTime>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val year = random.nextInt(LocalDate.MIN.year, LocalDate.MAX.year)
        val dayOfYear = random.nextInt(1, Year.of(year).length())
        val hour = random.nextInt(HOURS_IN_DAY)
        val minute = random.nextInt(MINUTES_IN_HOUR)
        val date = LocalDate.ofYearDay(year, dayOfYear)
        return date.atTime(hour, minute)
    }
}
