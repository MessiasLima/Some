package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType

class ObjectResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.objectInstance != null
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        return kClass.objectInstance!!
    }
}
