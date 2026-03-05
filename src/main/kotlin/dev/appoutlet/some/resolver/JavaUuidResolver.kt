package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import java.util.UUID

class JavaUuidResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<UUID>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return UUID.randomUUID()
    }
}
