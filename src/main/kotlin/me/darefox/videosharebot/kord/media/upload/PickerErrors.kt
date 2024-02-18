package me.darefox.videosharebot.kord.media.upload

import dev.kord.rest.builder.message.EmbedBuilder
import me.darefox.cobaltik.models.PickerType
import me.darefox.videosharebot.tools.stringtransformers.MarkdownCodeBlock
import kotlin.math.min

sealed class PickerError: UploadError()

data class PickerTypeNotSupported(
    val type: PickerType,
): PickerError() {
    override val message: String = "${type} is not supported"
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
}

data class FailedToDownloadImages(
    val errorList: List<Pair<Int, String>>,
    val allImagesCount: Int
): PickerError() {
    override val message: String = "Failed to download ${errorList.size} images out of $allImagesCount"
    override val asDiscordMessageText: String
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null

    init {
        val header = "$message\n\n"
        val listString = StringBuilder()

        listString.append("Reasons:\n")
        for ((index, reason) in errorList) {
            listString.append("Image #${index+1}: $reason\n")
        }
        val maxCharacters = 250
        val reasons = listString.toString()
        var stripped = (reasons).substring(0, min(maxCharacters, reasons.length))

        if (stripped.length < reasons.length) {
            stripped += "..."
        }

        asDiscordMessageText = MarkdownCodeBlock().invoke(header + stripped)
    }
}