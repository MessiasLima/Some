package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class ValueClassResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isValue
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val constructor = kClass.primaryConstructor
            ?: error("Value class ${kClass.simpleName} has no primary constructor")
        
        val parameter = constructor.parameters.firstOrNull()
            ?: error("Value class ${kClass.simpleName} has no constructor parameters")
        
        val resolvedValue = chain.resolve(parameter.type)
        
        return constructor.call(resolvedValue)
    }
}
