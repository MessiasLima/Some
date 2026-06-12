package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60
private const val SECONDS_IN_MINUTE = 60
private const val START_YEAR = 1970
private const val END_YEAR = 2100

class JavaZonedDateTimeResolver(private val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<ZonedDateTime>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val year = random.nextInt(START_YEAR, END_YEAR + 1)
        val dayOfYear = random.nextInt(1, Year.of(year).length() + 1)
        val hour = random.nextInt(HOURS_IN_DAY)
        val minute = random.nextInt(MINUTES_IN_HOUR)
        val second = random.nextInt(SECONDS_IN_MINUTE)
        val date = LocalDate.ofYearDay(year, dayOfYear)
        return ZonedDateTime.of(date, LocalTime.of(hour, minute, second), ZoneOffset.UTC)
    }
}
