package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class JavaUuidResolver : Resolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<UUID>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return UUID.randomUUID()
    }
}
