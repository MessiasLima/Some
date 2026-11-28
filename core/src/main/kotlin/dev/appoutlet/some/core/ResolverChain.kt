package dev.appoutlet.some.core

import dev.appoutlet.some.config.NullableStrategy
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
 * @param nullableStrategy Strategy used when a circular reference is detected for a nullable type.
 */
class ResolverChain(
    val resolvers: List<Resolver>,
    nullableStrategy: NullableStrategy?,
) {
    private val nullableStrategy = nullableStrategy ?: NullableStrategy.default

    /**
     * Maps each requested [KType] to the first resolver that supports it, or to `null` when no resolver does.
     *
     * The cache is scoped to this chain, and therefore to the resolver configuration the chain was created for.
     * Resolver lookups are assumed to be stable for the chain's lifetime, so the same type is never scanned twice.
     */
    private val resolverCache = mutableMapOf<KType, Resolver?>()

    /**
     * Types currently being resolved, ordered from the outermost request to the current type.
     *
     * The property exposes a read-only [List] backed by a mutable list that is private to this chain. The backing
     * field is intentionally mutable so resolution can push and pop types, while callers only observe a read-only
     * type. The list is live: a caller that needs a stable snapshot, such as [FixtureContext], must copy it.
     */
    val stack: List<KType>
        field = mutableListOf<KType>()

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

        stack.add(type)

        try {
            val resolver = findResolver(type) ?: throw SomeUnresolvableTypeException(type)
            return resolver.resolve(type, this)
        } finally {
            stack.removeAt(stack.lastIndex)
        }
    }

    /**
     * Returns the first resolver that supports [type], caching the outcome for later lookups.
     *
     * A cached `null` records that no resolver supports [type], so unsupported types are not rescanned on repeated
     * resolution attempts.
     *
     * @param type Type whose resolver should be selected.
     * @return The first matching resolver, or `null` when none supports [type].
     */
    private fun findResolver(type: KType): Resolver? {
        if (resolverCache.containsKey(type)) {
            return resolverCache[type]
        }

        return resolvers.firstOrNull { it.canResolve(type) }
            .also { resolverCache[type] = it }
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
        val sameTypeDetected = stack.any { it.withNullability(false) == normalizedType }

        return when {
            sameTypeDetected.not() -> false
            type.isMarkedNullable -> true
            stack.last().isMarkedNullable &&
                stack.last().withNullability(false) == normalizedType -> false
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

        throw SomeCircularReferenceException(type, stack.toList())
    }
}
