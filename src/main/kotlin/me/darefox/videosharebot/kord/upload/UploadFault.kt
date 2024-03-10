package me.darefox.videosharebot.kord.upload

import io.ktor.utils.io.errors.*
import me.darefox.videosharebot.error.ExpectedFault

sealed class UploadFault(message: String?, cause: Throwable?) : ExpectedFault(message, cause)

sealed class CommonUploadFault(message: String?, cause: Throwable?) : UploadFault(message, cause)

class IOFault(message: String?, cause: IOException): CommonUploadFault(message, cause)
class CantGetFilenameFault(cause: Throwable? = null): CommonUploadFault("Can't get filename from http headers", cause)