package me.darefox.videosharebot.kord.media.optimization

import dev.kord.rest.builder.message.EmbedBuilder
import me.darefox.videosharebot.kord.BotError

sealed interface OptimizationError : BotError

data object FileIsTooBigAfterOptimization: OptimizationError {
    override val message: String = "File is too big even after optimization!"
    override val asDiscordMessageText: String = message
    override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
}