package dev.appoutlet.some.exception

import kotlin.reflect.KType

class SomeCircularReferenceException(type: KType, stack: List<KType>) :
    Exception("Circular reference detected for $type. Resolution stack: $stack")
