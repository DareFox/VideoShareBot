package me.darefox.videosharebot.kord.media.upload

import dev.kord.rest.builder.message.EmbedBuilder
import java.io.IOException

sealed class StreamError: UploadError()

data object FileIsTooBig: StreamError() {
    override val message: String = "File is too big!"
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
}

data class IOError(
    val ioException: IOException
): StreamError() {
    override val message: String = ioException.message ?: "null"
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
}