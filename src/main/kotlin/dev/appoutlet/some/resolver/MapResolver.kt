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
 * Resolves [Map] and [MutableMap] types using the active [CollectionStrategy].
 *
 * @param collectionStrategy Strategy for determining collection sizes. Defaults to [CollectionStrategy.default] when null.
 * @param random Random source used for determining map size within the configured range.
 */
class MapResolver(
    collectionStrategy: CollectionStrategy?,
    val random: Random
) : TypeResolver {
    private val collectionStrategy = collectionStrategy ?: CollectionStrategy.default

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Map::class)
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val keyType = type.arguments.getOrNull(0)?.type
            ?: error("Star projection not supported in Map key")
        val valueType = type.arguments.getOrNull(1)?.type
            ?: error("Star projection not supported in Map value")

        val size = random.nextInt(
            collectionStrategy.sizeRange.first,
            collectionStrategy.sizeRange.last + 1
        )

        val isMutable = type.isSubtypeOf(typeOf<MutableMap<*, *>>())

        val entries = (1..size).map {
            chain.resolve(keyType) to chain.resolve(valueType)
        }

        return if (isMutable) {
            mutableMapOf<Any?, Any?>().apply { putAll(entries) }
        } else {
            entries.toMap()
        }
    }
}
