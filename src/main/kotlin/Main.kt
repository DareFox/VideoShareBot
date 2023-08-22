import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents

suspend fun main(args: Array<String>) {
    val bot = ExtensibleBot(EnvironmentConfig.discordToken) {
        intents {
            +Intents.nonPrivileged
            +Intent.GuildMessages
            +Intent.DirectMessages
        }
    }

    bot.start()
}