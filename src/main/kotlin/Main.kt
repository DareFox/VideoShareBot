import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import extension.MessageListener


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
    }

    bot.start()
}