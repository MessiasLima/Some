package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public class CharResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean = type == typeOf<Char>()

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        return ('a'..'z').random(context.random)
    }
}
