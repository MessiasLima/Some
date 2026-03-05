package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ShortResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Short>()

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return context.random.nextInt().toShort()
    }
}
