package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class BooleanResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Boolean>()

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return context.random.nextBoolean()
    }
}
