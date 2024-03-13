package me.darefox.videosharebot.extensions

import kotlinx.coroutines.CancellationException
import mu.KLogger
import mu.KotlinLogging

/**
 * Creates a Kotlin logger for this object.
 *
 * @return A [KLogger] instance.
 */
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

/**
 * Logs the cancellation event, providing details about the cancellation.
 *
 * @param cancellation The [CancellationException] representing the cancellation event.
 */
fun KLogger.logCancel(cancellation: CancellationException) {
    logCompletion(cancellation)
}

/**
 * Logs the completion event, indicating whether it was successful or cancelled.
 *
 * @param cancellation The [CancellationException] representing the cancellation event. If null, the completion is considered successful.
 */
fun KLogger.logCompletion(cancellation: CancellationException?) {
    if (cancellation == null) {
        debug { "Completed successfully" }
    } else {
        debug { "Cancelled by $cancellation" }
    }
}

