package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.FixtureContext
import dev.appoutlet.some.core.TypeResolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class SealedClassResolver : TypeResolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.isSealed
    }

    override fun resolve(type: KType, context: FixtureContext, chain: ResolverChain): Any? {
        val kClass = type.classifier as KClass<*>
        val leafSubclasses = collectLeafSubclasses(kClass)
        val chosenSubclass = leafSubclasses.random(context.random)
        return chain.resolve(chosenSubclass.createType(emptyList(), false), context)
    }

    private fun collectLeafSubclasses(kClass: KClass<*>): List<KClass<*>> {
        val directSubclasses = kClass.sealedSubclasses
        if (directSubclasses.isEmpty()) {
            return listOf(kClass)
        }
        return directSubclasses.flatMap { subclass ->
            collectLeafSubclasses(subclass)
        }
    }
}
