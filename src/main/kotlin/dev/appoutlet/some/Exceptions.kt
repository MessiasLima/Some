package dev.appoutlet.some

import kotlin.reflect.KType

public class SomeCircularReferenceException(type: KType, stack: List<KType>) :
    Exception("Circular reference detected for $type. Resolution stack: $stack")

public class SomeUnresolvableTypeException(type: KType) :
    Exception("No resolver found for type $type. Register a custom factory using SomeConfig.register<T> { ... }")
