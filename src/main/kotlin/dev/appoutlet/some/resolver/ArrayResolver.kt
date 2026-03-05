package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KType

public class ArrayResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        return type.toString().endsWith("Array<*>")
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val elementType = type.arguments.firstOrNull()?.type
            ?: error("Star projection not supported in Array")
        
        val size = context.random.nextInt(
            context.config.collectionStrategy.sizeRange.first,
            context.config.collectionStrategy.sizeRange.last + 1
        )
        
        return Array(size) { chain.resolve(elementType, context) }
    }
}
