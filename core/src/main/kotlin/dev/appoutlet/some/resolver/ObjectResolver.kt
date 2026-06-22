package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType

class ObjectResolver : Resolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.objectInstance != null
    }

    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        return kClass.objectInstance!!
    }
}
