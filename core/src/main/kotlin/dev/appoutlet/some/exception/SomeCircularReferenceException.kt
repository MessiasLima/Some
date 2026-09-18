package dev.appoutlet.some.exception

import kotlin.reflect.KType

class SomeCircularReferenceException(val circularType: KType, val stack: List<KType>) :
    Exception("Circular reference detected for $circularType. Resolution stack: $stack")
