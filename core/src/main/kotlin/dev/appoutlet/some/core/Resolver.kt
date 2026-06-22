package dev.appoutlet.some.core

import kotlin.reflect.KType

/**
 * Resolves a requested [KType] into a generated value.
 */
interface Resolver {
    /**
     * Returns whether this resolver can generate a value for [type].
     */
    fun canResolve(type: KType): Boolean

    /**
     * Generates a value for [type], delegating to [chain] for nested types when needed.
     */
    fun resolve(type: KType, chain: ResolverChain): Any?
}
