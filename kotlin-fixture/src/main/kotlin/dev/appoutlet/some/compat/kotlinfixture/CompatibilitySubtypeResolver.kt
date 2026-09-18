package dev.appoutlet.some.compat.kotlinfixture

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

internal class CompatibilitySubtypeResolver(
    private val subTypes: Map<KClass<*>, KClass<*>>,
) : Resolver {
    private val resolvingSuperTypes = ThreadLocal.withInitial { mutableSetOf<KClass<*>>() }

    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass in subTypes && kClass !in resolvingSuperTypes.get()
    }

    @Suppress("ReturnCount")
    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val kClass = type.classifier as? KClass<*> ?: return null
        val subClass = subTypes[kClass] ?: return null
        val activeSet = resolvingSuperTypes.get()

        require(subClass.isSubclassOf(kClass)) {
            "Configured subType ${subClass.qualifiedName} is not a subclass of ${kClass.qualifiedName}"
        }

        val targetType = if (subClass.typeParameters.isNotEmpty() && type.arguments.isNotEmpty()) {
            subClass.createType(
                arguments = type.arguments.take(subClass.typeParameters.size),
                nullable = type.isMarkedNullable
            )
        } else {
            subClass.createType(nullable = type.isMarkedNullable)
        }

        activeSet.add(kClass)
        try {
            val resolved = chain.resolve(targetType)
            if (resolved != null && !type.isMarkedNullable) {
                check(kClass.isInstance(resolved)) {
                    "Resolved subtype instance $resolved is not assignable to ${kClass.qualifiedName}"
                }
            }
            return resolved
        } finally {
            activeSet.remove(kClass)
        }
    }
}
