package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class SetResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Set::class)
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val elementType = type.arguments.firstOrNull()?.type
            ?: error("Star projection not supported in Set")
        
        val size = context.random.nextInt(
            context.config.collectionStrategy.sizeRange.first,
            context.config.collectionStrategy.sizeRange.last + 1
        )
        
        val isMutable = type.toString().startsWith("kotlin.collections.MutableSet")
        
        val elements = (1..size).map { chain.resolve(elementType, context) }
        
        return if (isMutable) {
            mutableSetOf<Any?>().apply { addAll(elements) }
        } else {
            elements.toSet()
        }
    }
}
