package me.darefox.videosharebot.kord.media.upload

import me.darefox.videosharebot.kord.media.optimization.OptimizationFault

sealed class StreamFault(message: String?, cause: Throwable?) : UploadFault(message, cause)

class FileIsTooBigFault(cause: Throwable? = null): StreamFault("File is too big!", cause)
class StreamOptimizationFault(
    val error: OptimizationFault
): StreamFault(error.message, error.cause)
