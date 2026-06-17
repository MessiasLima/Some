package dev.appoutlet.some.core

import kotlin.reflect.KClass

interface Strategy {
    val key: KClass<out Strategy>
}
