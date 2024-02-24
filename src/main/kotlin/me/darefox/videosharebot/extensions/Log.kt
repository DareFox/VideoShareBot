package me.darefox.videosharebot.extensions

import kotlinx.coroutines.CancellationException
import mu.KLogger
import mu.KotlinLogging

@OptIn(ExperimentalStdlibApi::class)
fun Any.createLogger(): KLogger {
    val name = javaClass.name
    val slicedName =
        when {
            name.contains("Kt$") -> name.substringBefore("Kt$")
            name.contains("$") -> name.substringBefore("$")
            else -> name
        }
    return KotlinLogging.logger("$slicedName@${hashCodeHex()}")
}

fun KLogger.logCancel(cancellation: CancellationException) {
    debug { "Cancelled by $cancellation" }
}