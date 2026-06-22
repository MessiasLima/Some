package dev.appoutlet.some.core

import dev.appoutlet.some.config.NullableStrategy
import dev.appoutlet.some.exception.SomeCircularReferenceException
import dev.appoutlet.some.exception.SomeUnresolvableTypeException
import kotlin.reflect.KType

/**
 * Resolution session that manages the type resolution chain and tracks circular dependencies.
 *
 * This class maintains a mutable stack of types currently being resolved to detect circular references.
 * Each call to `some()` creates a new instance of this session to ensure thread safety.
 *
 * @param resolvers Ordered resolver list. The first resolver that supports a type is used.
 * @param nullableStrategy Strategy used when a circular reference is detected for a nullable type.
 */
class ResolverChain(
    val resolvers: List<Resolver>,
    nullableStrategy: NullableStrategy?,
) {
    private val nullableStrategy = nullableStrategy ?: NullableStrategy.default
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
     * If the type would create a circular reference, [handleCircularReference] decides whether to return `null`
     * or throw based on the configured [NullableStrategy].
     *
     * @param type Type to resolve.
     * @return A generated value for [type], or `null` when nullable circular references are allowed.
     * @throws SomeCircularReferenceException when a circular reference cannot be represented as `null`.
     * @throws SomeUnresolvableTypeException when no resolver supports [type].
     */
    fun resolve(type: KType): Any? {
        if (detectCircularReference(type)) {
            return handleCircularReference(type)
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
     * Returns whether [type] would repeat a classifier already on the resolution stack.
     *
     * Classifier comparison treats `T` and `T?` as the same logical type, which is required to detect recursive
     * fields such as `data class Node(val next: Node?)`. The non-nullable case immediately after a nullable stack
     * entry is not circular: that is the expected path where [dev.appoutlet.some.resolver.NullableResolver]
     * unwraps `T?` into `T` before resolving the concrete value.
     */
    private fun detectCircularReference(type: KType): Boolean {
        val sameClassifierDetected = resolutionStack.any { it.classifier == type.classifier }

        return when {
            sameClassifierDetected.not() -> false
            type.isMarkedNullable -> true
            resolutionStack.last().isMarkedNullable -> false
            else -> true
        }
    }

    /**
     * Handles a circular reference that was detected for [type].
     *
     * Nullable circular references can be represented as `null` when the configured strategy allows it. Non-nullable
     * circular references always throw because there is no finite value that satisfies the type.
     *
     * @param type Type that would create a circular reference.
     * @return `null` when [type] is nullable and the nullable strategy allows null for circular references.
     * @throws SomeCircularReferenceException when the circular reference cannot be resolved as `null`.
     */
    private fun handleCircularReference(type: KType): Nothing? {
        val strategyAllowsNull = nullableStrategy is NullableStrategy.AlwaysNull ||
            nullableStrategy is NullableStrategy.NullOnCircularReference

        if (type.isMarkedNullable && strategyAllowsNull) {
            return null
        }

        throw SomeCircularReferenceException(type, resolutionStack.toList())
    }
}
