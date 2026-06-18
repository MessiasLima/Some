@file:Suppress("ForbiddenImport")

package dev.appoutlet.some.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.mutableLoggerConfigInit
import co.touchlab.kermit.platformLogWriter

private val loggerConfig = mutableLoggerConfigInit(
    minSeverity = Severity.Warn,
    logWriters = arrayOf(
        platformLogWriter(),
    ),
)

fun getLogger(tag: String): Logger {
    return Logger(loggerConfig).withTag(tag)
}
