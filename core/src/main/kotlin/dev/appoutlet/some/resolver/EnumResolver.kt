package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Resolves enum types by selecting a random constant.
 *
 * Enum constants are fixed after class loading, so the constants list is cached per enum class while the random
 * selection still happens on every resolution.
 *
 * @param random Random source used to pick an enum constant.
 */
class EnumResolver(val random: Random) : Resolver {
    private val constantsCache = ConcurrentHashMap<KClass<*>, List<Any>>()

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.java.isEnum
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        return constantsFor(kClass).random(random)
    }

    /**
     * Number of enum classes with cached constants. Intended for tests.
     */
    internal val cachedEnumCount: Int
        get() = constantsCache.size

    private fun constantsFor(kClass: KClass<*>): List<Any> {
        return constantsCache.computeIfAbsent(kClass) { enumClass ->
            val constants = requireNotNull(enumClass.java.enumConstants) {
                "Enum class ${enumClass.simpleName} has no constants"
            }
            constants.map { it as Any }
        }
    }
}
