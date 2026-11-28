package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * Resolves value classes by generating their underlying value and invoking the primary constructor.
 *
 * Value class constructors and parameter types are fixed after class loading, so they are cached per value class.
 * The underlying value is still resolved through the chain on every call.
 */
class ValueClassResolver : Resolver {
    private val valueClassCache = ConcurrentHashMap<KClass<*>, ValueClassInfo>()

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isValue
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val info = valueClassCache.computeIfAbsent(kClass) { valueClass ->
            createValueClassInfo(valueClass)
        }

        val resolvedValue = chain.resolve(info.parameterType)

        return requireNotNull(info.constructor.call(resolvedValue)) {
            "Value class constructor returned null for ${kClass.simpleName}"
        }
    }

    /**
     * Number of value classes with cached constructor information. Intended for tests.
     */
    internal val cachedValueClassCount: Int
        get() = valueClassCache.size

    private fun createValueClassInfo(kClass: KClass<*>): ValueClassInfo {
        val constructor = kClass.primaryConstructor
            ?: error("Value class ${kClass.simpleName} has no primary constructor")

        val parameter = constructor.parameters.firstOrNull()
            ?: error("Value class ${kClass.simpleName} has no constructor parameters")

        return ValueClassInfo(constructor, parameter.type)
    }

    private data class ValueClassInfo(
        val constructor: KFunction<*>,
        val parameterType: KType,
    )
}
