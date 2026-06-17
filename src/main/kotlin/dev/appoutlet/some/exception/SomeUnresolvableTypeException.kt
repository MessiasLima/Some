package dev.appoutlet.some.exception

import dev.appoutlet.some.config.SomeConfig
import dev.appoutlet.some.config.SomeConfigBuilder
import kotlin.reflect.KType

class SomeUnresolvableTypeException(type: KType) :
    Exception(
        "No resolver found for type $type. Register a custom type factory using SomeConfigBuilder.register<T> { ... }"
    )
