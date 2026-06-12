package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

/**
 * Resolves [Set] and [MutableSet] types using the active [CollectionStrategy].
 *
 * @param collectionStrategy Strategy for determining collection sizes.
 * Defaults to [CollectionStrategy.default] when null.
 * @param random Random source used for determining set size within the configured range.
 */
class SetResolver(
    collectionStrategy: CollectionStrategy?,
    val random: Random
) : TypeResolver {
    private val collectionStrategy = collectionStrategy ?: CollectionStrategy.default

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Set::class)
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val elementType = type.arguments.firstOrNull()?.type
            ?: error("Star projection not supported in Set")

        val size = random.nextInt(
            collectionStrategy.sizeRange.first,
            collectionStrategy.sizeRange.last + 1
        )

        val isMutable = type.isSubtypeOf(typeOf<MutableSet<*>>())

        val elements = (1..size).map { chain.resolve(elementType) }

        return if (isMutable) {
            mutableSetOf<Any?>().apply { addAll(elements) }
        } else {
            elements.toSet()
        }
    }
}
