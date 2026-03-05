package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

public class MapResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSubclassOf(Map::class)
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val keyType = type.arguments.getOrNull(0)?.type
            ?: error("Star projection not supported in Map key")
        val valueType = type.arguments.getOrNull(1)?.type
            ?: error("Star projection not supported in Map value")
        
        val size = context.random.nextInt(
            context.config.collectionStrategy.sizeRange.first,
            context.config.collectionStrategy.sizeRange.last + 1
        )
        
        val isMutable = type.toString().startsWith("kotlin.collections.MutableMap")
        
        val entries = (1..size).map {
            chain.resolve(keyType, context) to chain.resolve(valueType, context)
        }
        
        return if (isMutable) {
            mutableMapOf<Any?, Any?>().apply { putAll(entries) }
        } else {
            entries.toMap()
        }
    }
}
