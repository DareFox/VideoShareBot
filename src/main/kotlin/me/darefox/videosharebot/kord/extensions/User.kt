package me.darefox.videosharebot.kord.extensions

import dev.kord.core.entity.User
import me.darefox.videosharebot.bot

/**
 * Checks if this [User] object represents the same user as the bot itself.
 *
 * @return `true` if the user is the bot itself, `false` otherwise.
 */
fun User.isMe(): Boolean {
    return this.id == bot.kordRef.selfId
}
