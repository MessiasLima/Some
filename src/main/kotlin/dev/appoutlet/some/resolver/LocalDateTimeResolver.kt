package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.time.LocalDateTime

public class LocalDateTimeResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("LocalDateTime")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val dayOfYear = context.random.nextInt(1, 366)
        val hour = context.random.nextInt(24)
        val minute = context.random.nextInt(60)
        return LocalDateTime.of(2024, 1, dayOfYear, hour, minute)
    }
}
