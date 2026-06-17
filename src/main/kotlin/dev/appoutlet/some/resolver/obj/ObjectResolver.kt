package dev.appoutlet.some.resolver.obj

import dev.appoutlet.some.core.ResolverChain
import dev.appoutlet.some.core.TypeResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Resolves Kotlin `object` declarations by returning their singleton instance.
 *
 * A type is supported when its classifier is a [KClass] that represents an `object`
 * (i.e., [KClass.objectInstance] is non-null).
 */
class ObjectResolver : TypeResolver {
    /**
     * Returns `true` when [type] represents a Kotlin `object` declaration.
     *
     * @param type The type to inspect.
     * @return `true` if the type's classifier is a [KClass] with a non-null [KClass.objectInstance].
     */
    override fun canResolve(type: KType): Boolean {
        val kClass = type.classifier as? KClass<*> ?: return false
        return kClass.objectInstance != null
    }

    /**
     * Returns the singleton instance of the `object` described by [type].
     *
     * @param type The object type to resolve.
     * @param chain Unused by this resolver.
     * @return The singleton instance of the object.
     */
    override fun resolve(type: KType, chain: ResolverChain): Any {
        val kClass = type.classifier as KClass<*>
        return kClass.objectInstance!!
    }
}
