package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class DataClassResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        
        if (kClass.primaryConstructor == null) return false
        if (kClass.isSubclassOf(List::class) || kClass.isSubclassOf(Set::class) || kClass.isSubclassOf(Map::class)) {
            return false
        }
        if (kClass.isSubclassOf(String::class)) return false
        if (kClass.isSubclassOf(Number::class)) return false
        if (kClass.isSubclassOf(Boolean::class)) return false
        if (kClass.isSubclassOf(Char::class)) return false
        
        return true
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        val constructor = kClass.primaryConstructor
            ?: error("No primary constructor found for ${kClass.simpleName}")
        
        val typeArgMap = buildTypeArgMap(kClass, type)
        
        val args = constructor.parameters
            .filter { !it.isOptional }
            .associateWith { param ->
                val paramType = param.type
                val resolvedType = typeArgMap[paramType.toString()] ?: paramType
                chain.resolve(resolvedType)
            }
        
        return constructor.callBy(args)
    }
    
    private fun buildTypeArgMap(kClass: KClass<*>, type: KType): Map<String, KType> {
        return kClass.typeParameters
            .zip(type.arguments)
            .associate { (param, projection) -> 
                param.name to (projection.type ?: param.createType(emptyList(), false)) 
            }
    }
}
