package dev.appoutlet.some.core

import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.exception.SomeUnresolvableTypeException
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Resolution session that manages the type resolution chain and tracks circular dependencies.
 *
 * This class maintains a mutable stack of types currently being resolved to detect circular references.
 * Each call to `some()` creates a new instance of this session to ensure thread safety.
 *
 * @param resolvers Ordered resolver list. The first resolver that supports a type is used.
 */
class ResolverChain(
    val resolvers: List<Resolver>,
) {
    private val resolutionStack = mutableListOf<KType>()

    /**
     * Returns an immutable snapshot of the current resolution stack.
     * Used by CustomTypeFactoryResolver to provide context to user type factories.
     */
    val stack: List<KType>
        get() = resolutionStack.toList()

    /**
     * Resolves a value for [type] using the first matching resolver.
     *
     * The type is added to the resolution stack while it is being resolved and removed even if resolution fails.
     * If the type would create a circular reference, [SomeCircularReferenceException] is thrown.
     *
     * @param type Type to resolve.
     * @return A generated value for [type].
     * @throws SomeCircularReferenceException when a circular reference is detected.
     * @throws SomeUnresolvableTypeException when no resolver supports [type].
     */
    fun resolve(type: KType): Any? {
        if (detectCircularReference(type)) {
            throw SomeCircularReferenceException(type, resolutionStack.toList())
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

    /**
     * Returns whether [type] would repeat a type already on the resolution stack.
     *
     * Full type comparison ignoring only outer nullability treats `T` and `T?` as the same logical type, which is
     * required to detect recursive fields such as `data class Node(val next: Node?)`. The non-nullable case
     * immediately after a nullable stack entry is not circular: that is the expected path where
     * [dev.appoutlet.some.resolver.NullableResolver] unwraps `T?` into `T` before resolving the concrete value.
     */
    private fun detectCircularReference(type: KType): Boolean {
        val normalizedType = type.withNullability(false)
        val sameTypeDetected = resolutionStack.any { it.withNullability(false) == normalizedType }

        return when {
            sameTypeDetected.not() -> false
            type.isMarkedNullable -> false
            resolutionStack.last().withNullability(false) == normalizedType &&
                resolutionStack.dropLast(1).none { it.withNullability(false) == normalizedType } -> false
            else -> true
        }
    }
}
