package me.darefox.videosharebot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import me.darefox.videosharebot.config.GlobalApplicationConfig
import me.darefox.videosharebot.config.PresenceAction
import me.darefox.videosharebot.kord.listeners.MessageListener

lateinit var _bot: ExtensibleBot
val bot by lazy { _bot }

suspend fun main(args: Array<String>) {
    val discordConfig = GlobalApplicationConfig.discord
    _bot = ExtensibleBot(discordConfig.token) {
        intents {
            +Intents.NON_PRIVILEGED
            +Intent.GuildMessages
            +Intent.DirectMessages
        }
        extensions {
            add(::MessageListener)
        }
        presence {
            status = discordConfig.presenceStatus.status
            when (val action = discordConfig.presenceAction) {
                is PresenceAction.Competing -> competing(action.name)
                is PresenceAction.Listening -> listening(action.name)
                is PresenceAction.Playing -> playing(action.name)
                is PresenceAction.Watching -> watching(action.name)
                PresenceAction.None -> {}
            }
        }
    }

    _bot.start()
}