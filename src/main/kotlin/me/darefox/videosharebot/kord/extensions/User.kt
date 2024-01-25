package me.darefox.videosharebot.kord.extensions

import dev.kord.core.entity.User
import me.darefox.videosharebot.bot

fun User.isMe(): Boolean {
    return this.id == bot.kordRef.selfId
}
