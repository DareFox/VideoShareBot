package me.darefox.videosharebot.kord.media.optimization

import me.darefox.videosharebot.error.ExpectedFault

sealed class OptimizationFault(message: String?, cause: Throwable?) : ExpectedFault(message, cause)

class FileTooBigAfterOptimizationFault(causedBy: Throwable? = null):
    OptimizationFault("File is too big even after optimization", causedBy)

class FFmpegOptimizationFault(message: String?, causedBy: Throwable? = null):
    OptimizationFault(message, causedBy)