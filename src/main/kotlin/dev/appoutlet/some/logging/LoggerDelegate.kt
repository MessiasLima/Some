@file:Suppress("ForbiddenImport")

package dev.appoutlet.some.logging

import co.touchlab.kermit.Logger
import kotlin.reflect.KProperty

class LoggerDelegate {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return getLogger(
            thisRef::class.simpleName ?: thisRef::class.qualifiedName ?: "Unknown class"
        )
    }
}

fun logger(): LoggerDelegate = LoggerDelegate()
