package extensions

import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.entity.Message
import dev.kord.core.kordLogger
import dev.kord.rest.builder.message.create.embed
import io.ktor.util.logging.*

suspend fun Message.sendErrorAsEmbed(err: Exception) {
    respond {
        embed {
            title = "ERROR"
            field("Class", true) {"`${err.javaClass.name}`"}
            field("Message", true) {"`${err.message.toString()}`"}
            field("Callstack", false) { "```kotlin\n${err.stackTraceToString()}```" }
            kordLogger.error(err)
        }
    }
}