package dev.appoutlet.some.resolver

import dev.appoutlet.some.core.Resolver
import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.exception.SomeSubtypeMappingException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Resolves a configured supertype to a specific concrete subtype.
 *
 * Unlike [SealedClassResolver], this resolver is deterministic and works for sealed types, abstract classes, and
 * non-sealed interfaces. The mapped subtype is resolved through the chain again, so its properties are generated
 * normally.
 *
 * When the requested parent carries type arguments and the mapped subtype has the same number of type parameters,
 * the parent arguments are preserved on the subtype. Otherwise, the subtype is created without type arguments, which
 * lets its parameters fall back to their declared upper bounds.
 *
 * @param subTypes Mapping from a parent class to the concrete subtype that should be generated for it.
 */
class SubtypeResolver(
    private val subTypes: Map<KClass<*>, KClass<*>>,
) : Resolver {
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass in subTypes
    }

    override fun resolve(type: KType, chain: ResolverChain): Any? {
        val parent = type.classifier as KClass<*>
        val subtype = subTypes[parent] ?: return null

        if (chain.stack.any { it.classifier == subtype }) {
            throw SomeSubtypeMappingException(parent, subtype, chain.stack.toList())
        }

        return chain.resolve(createSubtypeType(type, subtype))
    }

    private fun createSubtypeType(parent: KType, subtype: KClass<*>): KType {
        val typeArguments = if (subtype.typeParameters.size == parent.arguments.size) {
            parent.arguments
        } else {
            emptyList()
        }

        return subtype.createType(typeArguments, false)
    }
}
