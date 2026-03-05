package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType

class CustomFactoryResolver(
    private val factories: Map<KClass<*>, FixtureContext.() -> Any?>
) : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass in factories
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any? {
        val kClass = type.classifier as KClass<*>
        return factories[kClass]?.invoke(context)
    }
}
