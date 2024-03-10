package me.darefox.videosharebot.kord.media.upload

import me.darefox.cobaltik.models.PickerType

sealed class PickerFault(message: String?, cause: Throwable?) : UploadFault(message, cause)

class PickerTypeNotSupportedFault(
    type: PickerType,
    cause: Throwable? = null
): PickerFault("$type is not supported", cause)

class FailedToDownloadImagesFault(message: String?, cause: Throwable?) : PickerFault(message, cause) {
    constructor(
        errorList: List<Pair<Int, String>>,
        allImagesCount: Int,
        cause: Throwable? = null
    ): this(createMessage(errorList, allImagesCount), cause)

    companion object {
        private fun createMessage(errorList: List<Pair<Int, String>>, allImagesCount: Int): String {
            val message = "Failed to download ${errorList.size} images out of $allImagesCount"
            val header = "$message\n"
            val listString = StringBuilder()

            listString.append("\tReasons:\n")
            for ((index, reason) in errorList) {
                listString.append("\tImage #${index+1}: $reason\n")
            }
            return listString.toString()
        }
    }
}