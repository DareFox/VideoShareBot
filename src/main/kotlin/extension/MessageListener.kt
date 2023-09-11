package extension

import MimeMap
import cobalt.Cobalt
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import com.sun.nio.sctp.IllegalReceiveException
import dev.kord.core.event.message.MessageCreateEvent
import enhancements.toSafeFilename
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.*
import ktor
import parser.CompositeParser
import parser.TikTokParser
import parser.YoutubeShortsParser
import java.net.URL

class MessageListener : LoggerExtension("MessageListener") {
    val parser = CompositeParser(
        setOf(
            TikTokParser,
            YoutubeShortsParser
        )
    )

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
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

        val contentType = request.headers["Content-Type"] ?: throw IllegalReceiveException("No content-type")
        val extension =
            MimeMap[contentType] ?: throw IllegalReceiveException("Content-type $contentType is not supported")
        val filename = URL(shareUrl.first()).toSafeFilename() + extension

        event.message.respond {
            addFile(filename, ChannelProvider { request.content })
        }
    }
}