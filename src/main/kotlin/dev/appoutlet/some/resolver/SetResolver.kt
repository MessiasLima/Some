package dev.appoutlet.some.resolver

import dev.appoutlet.some.config.CollectionStrategy
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class SetResolver(
    private val collectionStrategy: CollectionStrategy = CollectionStrategy(),
    val random: Random
) : TypeResolver {
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

        // TODO is it possible to use typeOf or isSubclassOf here?
        val isMutable = type.toString().startsWith("kotlin.collections.MutableSet")
        
        val elements = (1..size).map { chain.resolve(elementType) }
        
        return if (isMutable) {
            mutableSetOf<Any?>().apply { addAll(elements) }
        } else {
            elements.toSet()
        }
    }
}
