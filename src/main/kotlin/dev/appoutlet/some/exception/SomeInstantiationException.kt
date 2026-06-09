package dev.appoutlet.some.exception

import kotlin.reflect.KClass

class SomeInstantiationException(
    kClass: KClass<*>,
    failures: List<String>,
) : Exception(
    "Failed to instantiate ${kClass.simpleName}. All constructors failed:\n${failures.joinToString("\n")}"
)
