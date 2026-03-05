package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.time.LocalDate

class LocalDateResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("LocalDate")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val dayOfYear = context.random.nextInt(1, 366)
        return LocalDate.ofYearDay(2024, dayOfYear)
    }
}
