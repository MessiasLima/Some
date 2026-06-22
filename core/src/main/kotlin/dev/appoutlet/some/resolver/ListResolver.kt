package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.StrategyProvider
import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.get
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

/**
 * Resolves [List] and [MutableList] types using the active [CollectionStrategy].
 *
 * @param strategyProvider Provider of all configured generation strategies.
 * @param random Random source used for determining list size within the configured range.
 */
class ListResolver(
    strategyProvider: StrategyProvider,
    private val random: Random
) : Resolver {
    private val collectionStrategy = strategyProvider.get<CollectionStrategy>() ?: CollectionStrategy.default

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(List::class)
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val elementType = requireNotNull(type.arguments.firstOrNull()?.type) {
            "Star projection not supported in List"
        }

        val size = random.nextInt(
            collectionStrategy.sizeRange.first,
            collectionStrategy.sizeRange.last + 1
        )

        val isMutable = type.isSubtypeOf(typeOf<MutableList<*>>())

        return if (isMutable) {
            MutableList(size) { chain.resolve(elementType) }
        } else {
            List(size) { chain.resolve(elementType) }
        }
    }
}
