package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class ListResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(List::class)
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val elementType = type.arguments.firstOrNull()?.type
            ?: error("Star projection not supported in List")
        
        val size = context.random.nextInt(
            context.config.collectionStrategy.sizeRange.first,
            context.config.collectionStrategy.sizeRange.last + 1
        )
        
        val isMutable = type.toString().startsWith("kotlin.collections.MutableList")
        
        return if (isMutable) {
            MutableList(size) { chain.resolve(elementType, context) }
        } else {
            List(size) { chain.resolve(elementType, context) }
        }
    }
}
