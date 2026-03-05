package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import java.util.UUID

public class UuidResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().contains("UUID")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return UUID.randomUUID()
    }
}
