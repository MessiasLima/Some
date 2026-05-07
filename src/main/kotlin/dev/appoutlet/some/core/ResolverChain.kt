package dev.appoutlet.some.core

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.exception.SomeUnresolvableTypeException
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Resolution session that manages the type resolution chain and tracks circular dependencies.
 *
 * This class maintains a mutable stack of types currently being resolved to detect circular references.
 * Each call to `some()` creates a new instance of this session to ensure thread safety.
 */
class ResolverChain(
    val resolvers: List<TypeResolver>,
    private val nullableStrategy: NullableStrategy = NullableStrategy.NullOnCircularReference
) {
    private val resolutionStack = mutableListOf<KType>()

    /**
     * Returns an immutable snapshot of the current resolution stack.
     * Used by CustomFactoryResolver to provide context to user factories.
     */
    val stack: List<KType>
        get() = resolutionStack.toList()

    fun resolve(type: KType): Any? {
        if (type in resolutionStack) {
            throw SomeCircularReferenceException(type, resolutionStack.toList())
        }

        if (nullableStrategy is NullableStrategy.NullOnCircularReference && type.isMarkedNullable) {
            if (isCircularIgnoringNullability(type)) {
                return null
            }
        }

        resolutionStack.add(type)

        try {
            for (resolver in resolvers) {
                if (resolver.canResolve(type)) {
                    return resolver.resolve(type, this)
                }
            }

            throw SomeUnresolvableTypeException(type)
        } finally {
            resolutionStack.removeAt(resolutionStack.lastIndex)
        }
    }

    private fun isCircularIgnoringNullability(type: KType): Boolean {
        val nonNullableType = type.classifier?.createType(type.arguments, false) ?: type
        return resolutionStack.any {
            val otherNonNullable = it.classifier?.createType(it.arguments, false) ?: it
            otherNonNullable == nonNullableType
        }
    }
}
