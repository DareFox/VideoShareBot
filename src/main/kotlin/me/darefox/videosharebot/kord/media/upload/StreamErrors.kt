package me.darefox.videosharebot.kord.media.upload

import dev.kord.rest.builder.message.EmbedBuilder
import me.darefox.videosharebot.ffmpeg.FFmpegError
import me.darefox.videosharebot.kord.media.optimization.OptimizationError
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

data class StreamOptimizationError(
    val error: OptimizationError
): StreamError() {
    override val message: String = error.message
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
}

data object CantGetFilename: StreamError() {
    override val message: String = "Can't get filename from http headers"
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null

}