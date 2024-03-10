package me.darefox.videosharebot.error

open class ExpectedFault(
    message: String?,
    causedBy: Throwable?
): Exception(message, causedBy)