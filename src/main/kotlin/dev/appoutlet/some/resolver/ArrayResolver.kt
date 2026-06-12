package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Resolves [Array] types using the active [CollectionStrategy].
 *
 * @param collectionStrategy Strategy for determining array sizes. Defaults to [CollectionStrategy.default] when null.
 * @param random Random source used for determining array size within the configured range.
 */
class ArrayResolver(
    collectionStrategy: CollectionStrategy?,
    val random: Random
) : TypeResolver {
    private val collectionStrategy = collectionStrategy ?: CollectionStrategy.default

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.qualifiedName == "kotlin.Array"
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val elementType = type.arguments.firstOrNull()?.type
            ?: error("Star projection not supported in Array")

        val size = random.nextInt(
            collectionStrategy.sizeRange.first,
            collectionStrategy.sizeRange.last + 1
        )

        return Array(size) { chain.resolve(elementType) }
    }
}
