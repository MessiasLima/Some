package dev.appoutlet.some.core

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Caches the result of [KClass.isSubclassOf] lookups.
 *
 * Class hierarchies are fixed after class loading, so the result of a subclass check for the same
 * subclass/superclass pair never changes. The cache is backed by a [ConcurrentHashMap], making it safe to
 * share between resolver instances and across threads.
 *
 * @param relationCheck Performs the actual subclass check. Exposed for tests that need to observe cache hits.
 */
internal class SubclassRelationCache(
    private val relationCheck: (subclass: KClass<*>, superclass: KClass<*>) -> Boolean = { subclass, superclass ->
        subclass.isSubclassOf(superclass)
    },
) {
    private val relations = ConcurrentHashMap<Relation, Boolean>()

    /**
     * Returns whether [subclass] is a subclass of [superclass], reusing a cached result when available.
     */
    fun isSubclassOf(subclass: KClass<*>, superclass: KClass<*>): Boolean {
        return relations.computeIfAbsent(Relation(subclass, superclass)) {
            relationCheck(subclass, superclass)
        }
    }

    /**
     * Number of cached subclass relations. Intended for tests.
     */
    internal val size: Int
        get() = relations.size

    private data class Relation(
        val subclass: KClass<*>,
        val superclass: KClass<*>,
    )

    internal companion object {
        /**
         * Process-wide cache used by [isSubclassOfCached].
         */
        val shared = SubclassRelationCache()
    }
}

/**
 * Returns whether this class is a subclass of [base], using the shared [SubclassRelationCache].
 *
 * Prefer this helper over importing `kotlin.reflect.full.isSubclassOf` directly so repeated subclass checks
 * across resolvers reuse cached results.
 */
internal fun KClass<*>.isSubclassOfCached(base: KClass<*>): Boolean {
    return SubclassRelationCache.shared.isSubclassOf(this, base)
}
