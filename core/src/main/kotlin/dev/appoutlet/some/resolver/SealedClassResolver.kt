package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Resolves sealed classes and interfaces by selecting a random leaf subclass.
 *
 * Sealed hierarchies are fixed after class loading, so the leaf subclass list is cached per sealed class.
 * Selection still happens on every resolution, and the generated instance is created through the resolver chain.
 *
 * @param random Random source used to select a leaf subclass.
 */
class SealedClassResolver(val random: Random) : Resolver {
    private val leafSubclassesCache = ConcurrentHashMap<KClass<*>, List<KClass<*>>>()

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSealed
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val kClass = type.classifier as KClass<*>
        val leafSubclasses = leafSubclassesFor(kClass)
        val chosenSubclass = leafSubclasses.random(random)
        return chain.resolve(chosenSubclass.createType(emptyList(), false))
    }

    /**
     * Number of sealed classes with cached leaf lists. Intended for tests.
     */
    internal val cachedSealedCount: Int
        get() = leafSubclassesCache.size

    private fun leafSubclassesFor(kClass: KClass<*>): List<KClass<*>> {
        return leafSubclassesCache.computeIfAbsent(kClass) { sealedClass ->
            collectLeafSubclasses(sealedClass)
        }
    }

    private fun collectLeafSubclasses(kClass: KClass<*>): List<KClass<*>> {
        val directSubclasses = kClass.sealedSubclasses
        if (directSubclasses.isEmpty()) {
            return listOf(kClass)
        }
        return directSubclasses.flatMap { subclass ->
            collectLeafSubclasses(subclass)
        }
    }
}
