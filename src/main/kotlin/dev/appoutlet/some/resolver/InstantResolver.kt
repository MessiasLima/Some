package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.time.Instant

public class InstantResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("Instant")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val epochSecond = context.random.nextLong(0, 4102444800) // 2100-01-01
        return Instant.ofEpochSecond(epochSecond)
    }
}
