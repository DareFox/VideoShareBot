package me.darefox.videosharebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import me.darefox.videosharebot.kord.listeners.MessageListener


suspend fun main(args: Array<String>) {
    val bot = ExtensibleBot(EnvironmentConfig.discordToken) {
        intents {
            +Intents.NON_PRIVILEGED
            +Intent.GuildMessages
            +Intent.DirectMessages
        }
        extensions {
            add(::MessageListener)
        }
        presence {
            status = PresenceStatus.DoNotDisturb
            competing("чате с ссылками")
        }
    }

    bot.start()
}