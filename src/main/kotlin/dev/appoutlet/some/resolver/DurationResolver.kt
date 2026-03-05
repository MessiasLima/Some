package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.time.Duration

public class DurationResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("Duration")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val seconds = context.random.nextLong(0, 86400) // max 1 day
        return Duration.ofSeconds(seconds)
    }
}
