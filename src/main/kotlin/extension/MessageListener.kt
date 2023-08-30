package extension

import cobalt.Cobalt
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.event.message.MessageCreateEvent
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import ktor
import parser.CompositeParser
import parser.TikTokParser
import parser.YoutubeShortsParser
import java.io.File
import java.io.InputStream

class MessageListener : LoggerExtension("MessageListener") {
    val parser = CompositeParser(
        setOf(
            TikTokParser,
            YoutubeShortsParser
        )
    )
    override suspend fun setup() {
        log.info { "setup" }
        event<MessageCreateEvent> {
            action {
                log.info { "test" }
                actionImpl()
            }
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun EventContext<MessageCreateEvent>.actionImpl() {
        if (event.member?.isBot == true) return
        val shareUrl = parser.parse(event.message.content)

        if (shareUrl.isEmpty()) return
        val downloadUrl = Cobalt.requestVideo(shareUrl.first()) ?: return
        val request = ktor.get(downloadUrl)

        event.message.respond {
            addFile("video.mp4", ChannelProvider { request.content })
        }
    }
}