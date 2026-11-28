package dev.appoutlet.some.core

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

/**
 * Caches the result of [KType.isSubtypeOf] lookups.
 *
 * Types are stable values, so the result of a subtype check for the same value/base pair never changes. The cache
 * is backed by a [ConcurrentHashMap], making it safe to share between resolver instances and across threads.
 *
 * @param relationCheck Performs the actual subtype check. Exposed for tests that need to observe cache hits.
 */
internal class SubtypeRelationCache(
    private val relationCheck: (value: KType, base: KType) -> Boolean = { value, base ->
        value.isSubtypeOf(base)
    },
) {
    private val relations = ConcurrentHashMap<Relation, Boolean>()

    /**
     * Returns whether [value] is a subtype of [base], reusing a cached result when available.
     */
    fun isSubtypeOf(value: KType, base: KType): Boolean {
        return relations.computeIfAbsent(Relation(value, base)) {
            relationCheck(value, base)
        }
    }

    /**
     * Number of cached subtype relations. Intended for tests.
     */
    internal val size: Int
        get() = relations.size

    private data class Relation(
        val value: KType,
        val base: KType,
    )

    internal companion object {
        /**
         * Process-wide cache used by [isSubtypeOfCached].
         */
        val shared = SubtypeRelationCache()
    }
}

/**
 * Returns whether this type is a subtype of [base], using the shared [SubtypeRelationCache].
 *
 * Prefer this helper over importing `kotlin.reflect.full.isSubtypeOf` directly so repeated subtype checks
 * across resolvers reuse cached results.
 */
internal fun KType.isSubtypeOfCached(base: KType): Boolean {
    return SubtypeRelationCache.shared.isSubtypeOf(this, base)
}
