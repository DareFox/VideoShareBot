package extension

import MimeMap
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.suppressEmbeds
import com.sun.nio.sctp.IllegalReceiveException
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.create.embed
import enhancements.sendErrorAsEmbed
import enhancements.toSafeFilename
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.*
import io.ktor.utils.io.errors.*
import io.ktor.utils.io.jvm.javaio.*
import ktor
import match.*
import me.darefox.cobaltik.wrapper.Cobalt
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.cobaltik.wrapper.StreamResponse
import java.net.URL

class MessageListener : LoggerExtension("MessageListener") {
    val parser = CompositeMatcher(
        setOf(
            TikTokMatcher,
            YoutubeShortsMatcher,
            TwitterMatcher,
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
    private suspend fun EventContext<MessageCreateEvent>.stream(streamResponse: StreamResponse, url: String) {
        val request = try {
            ktor.get(streamResponse.streamUrl)
        } catch (err: IOException) {
            event.message.sendErrorAsEmbed(err)
            return
        }

        val contentType = request.headers["Content-Type"] ?: "video/mp4"
        val extension =
            MimeMap[contentType] ?: throw IllegalReceiveException("Content-type $contentType is not supported")
        val filename = URL(url).toSafeFilename() + extension

        event.message.respond {
//            addFile("", ChannelProvider { "".byteInputStream().toByteReadChannel() })
            addFile(filename, ChannelProvider { request.content })
        }
        event.message.edit {
            suppressEmbeds = true
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.redirect(redirectResponse: RedirectResponse) {
        event.message.respond(redirectResponse.redirectUrl)
        event.message.edit {
            suppressEmbeds = true
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun EventContext<MessageCreateEvent>.actionImpl() {
        if (event.member?.isBot == true) return
        val shareUrl = parser.parse(event.message.content)

        if (shareUrl.isEmpty()) return
        val client = Cobalt("https://co.wuk.sh/")
        val url = when (val response = client.request(shareUrl.first())) {
            is RedirectResponse -> redirect(response)
            is StreamResponse -> stream(response, shareUrl.first())
            else -> return
        }



    }
}