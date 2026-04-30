package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import java.time.LocalDate
import java.time.Year
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class LocalDateResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<LocalDate>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val year = random.nextInt(LocalDate.MIN.year, LocalDate.MAX.year)
        val dayOfYear = random.nextInt(1, Year.of(year).length())
        return LocalDate.ofYearDay(year, dayOfYear)
    }
}
