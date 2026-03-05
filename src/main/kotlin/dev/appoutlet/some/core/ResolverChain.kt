package dev.appoutlet.some.core

import dev.appoutlet.some.SomeCircularReferenceException
import dev.appoutlet.some.SomeUnresolvableTypeException
import kotlin.reflect.KType

public class ResolverChain(public val resolvers: List<TypeResolver>) {
    public fun resolve(type: KType, context: FixtureContext): Any? {
        if (context.isCircular(type)) {
            throw SomeCircularReferenceException(type, context.resolutionStack)
        }
        val nextContext = context.push(type)
        
        for (resolver in resolvers) {
            if (resolver.canResolve(type)) {
                return resolver.resolve(type, nextContext, this)
            }
        }
        
        throw SomeUnresolvableTypeException(type)
    }
}
