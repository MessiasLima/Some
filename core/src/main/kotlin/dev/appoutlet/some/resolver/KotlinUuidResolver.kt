package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.Resolver
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KotlinUuidResolver : Resolver {
    override fun canResolve(type: KType): Boolean {
        return type == typeOf<Uuid>()
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        return Uuid.random()
    }
}
