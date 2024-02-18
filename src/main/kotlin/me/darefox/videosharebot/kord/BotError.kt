package me.darefox.videosharebot.kord

import dev.kord.rest.builder.message.EmbedBuilder

interface BotError {
    val message: String
    val asDiscordMessageText: String
    val discordEmbedBuilder: (EmbedBuilder.() -> Unit)?
}