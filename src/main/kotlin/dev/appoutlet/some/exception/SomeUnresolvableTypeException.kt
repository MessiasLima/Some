package dev.appoutlet.some.exception

import kotlin.reflect.KType

class SomeUnresolvableTypeException(type: KType) :
    Exception("No resolver found for type $type. Register a custom factory using SomeConfig.register<T> { ... }")
