package dev.appoutlet.some.core

import kotlin.reflect.KType

interface TypeResolver {
    fun canResolve(type: KType): Boolean
    fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any?
}
