package dev.appoutlet.some.exception

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Thrown when a configured subtype mapping would resolve the same subtype repeatedly.
 *
 * Self-mappings and cyclic mappings cannot produce a finite value, so resolution fails instead of recursing until
 * a stack overflow.
 */
class SomeSubtypeMappingException(
    parent: KClass<*>,
    subtype: KClass<*>,
    stack: List<KType>,
) : Exception(
    "Cyclic subtype mapping detected for ${parent.simpleName} -> ${subtype.simpleName}. " +
        "Check for self-mappings or cyclic subtype mappings. Resolution stack: $stack"
)
