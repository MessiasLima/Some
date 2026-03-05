package dev.appoutlet.some.core

import kotlin.reflect.KType

public interface TypeResolver {
    public fun canResolve(type: KType): Boolean
    public fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any?
}
