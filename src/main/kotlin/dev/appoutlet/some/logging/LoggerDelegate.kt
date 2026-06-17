@file:Suppress("ForbiddenImport")

package dev.appoutlet.some.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LoggerDelegate : ReadOnlyProperty<Any?, Logger> {
    private var logger: Logger? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (logger == null) {
            val tag = thisRef?.let { it::class.simpleName } ?: "Global"
            logger = getLogger(tag)
        }
        return logger!!
    }
}

fun logger(): LoggerDelegate = LoggerDelegate()

fun Logger.d(throwable: Throwable? = null, message: () -> String) {
    this.log(Severity.Debug, tag = "", throwable = throwable, message = message())
}

fun Logger.w(throwable: Throwable? = null, message: () -> String) {
    this.log(Severity.Warn, tag = "", throwable = throwable, message = message())
}

fun Logger.e(throwable: Throwable? = null, message: () -> String) {
    this.log(Severity.Error, tag = "", throwable = throwable, message = message())
}
