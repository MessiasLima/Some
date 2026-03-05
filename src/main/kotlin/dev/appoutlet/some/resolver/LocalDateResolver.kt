package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import java.time.LocalDate

class LocalDateResolver(val random: Random) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<LocalDate>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val dayOfYear = random.nextInt(1, 366)
        return LocalDate.ofYearDay(2024, dayOfYear)
    }
}
