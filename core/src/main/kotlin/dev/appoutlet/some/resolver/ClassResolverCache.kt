package dev.appoutlet.some.resolver

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Caches constructor metadata used by [ClassResolver].
 *
 * Only the constructor list returned by reflection is cached. Constructor arguments, constructed objects, and
 * constructor selections are never cached, so requested generic arguments, the default-value strategy, property
 * factories, and random constructor failures keep their existing per-resolution behavior.
 *
 * @param constructorLookup Retrieves constructors for a class. Exposed for tests that need to observe cache hits.
 */
internal class ClassResolverCache(
    private val constructorLookup: (KClass<*>) -> List<KFunction<*>> = { kClass: KClass<*> ->
        kClass.constructors.toList()
    },
) {
    private val constructors = ConcurrentHashMap<KClass<*>, List<KFunction<*>>>()

    /**
     * Returns the constructors of [kClass] in declaration order, reusing the cached list when available.
     */
    fun constructorsFor(kClass: KClass<*>): List<KFunction<*>> {
        return constructors.computeIfAbsent(kClass) { constructorLookup(it) }
    }

    /**
     * Number of classes with cached constructor metadata. Intended for tests.
     */
    internal val size: Int
        get() = constructors.size
}
