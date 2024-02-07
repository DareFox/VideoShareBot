package me.darefox.videosharebot.kord.extensions

import dev.kord.core.entity.Message

data class BotMessage(
    val ref: Message
) {
    init {
        require(ref.author?.isMe() == true) { "Message author is not made by me (bot), message belongs to ${ref.author}" }
    }
}

fun Message.asBotMessage(): BotMessage = BotMessage(this)