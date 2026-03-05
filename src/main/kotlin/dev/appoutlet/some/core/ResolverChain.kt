package dev.appoutlet.some.core

import dev.appoutlet.some.SomeCircularReferenceException
import dev.appoutlet.some.SomeUnresolvableTypeException
import kotlin.reflect.KType

class ResolverChain(
    val resolvers: List<TypeResolver>,
    val resolutionStack: List<KType> = emptyList()
) {
    fun resolve(type: KType): Any? {
        if (isCircular(type)) {
            throw SomeCircularReferenceException(type, resolutionStack)
        }

        val nextChain = push(type)
        
        for (resolver in resolvers) {
            if (resolver.canResolve(type)) {
                return resolver.resolve(type, nextChain)
            }
        }
        
        throw SomeUnresolvableTypeException(type)
    }
    
    private fun push(type: KType): ResolverChain {
        return ResolverChain(resolvers, resolutionStack + type)
    }
    
    private fun isCircular(type: KType): Boolean {
        return resolutionStack.any { it == type }
    }
}
