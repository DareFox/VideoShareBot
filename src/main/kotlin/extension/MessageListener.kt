package extension

import MimeMap
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import com.sun.nio.sctp.IllegalReceiveException
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import enhancements.sendErrorAsEmbed
import enhancements.toSafeFilename
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.util.*
import io.ktor.utils.io.errors.*
import ktor
import match.*
import match.services.*
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
            RedditMatcher,
            VkMatcher
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
    private suspend fun EventContext<MessageCreateEvent>.stream(streamResponse: StreamResponse, parseResult: CompositeMatcherResult) {
        val url = parseResult.url
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

    private suspend fun EventContext<MessageCreateEvent>.actionImpl() {
        if (event.member?.isBot == true) return
        val shareUrl = parser.parse(event.message.content)

        if (shareUrl.isEmpty()) return
        val parseResult = shareUrl.first()

        log.info { "Trying to ask cobalt for $parseResult" }
        val client = Cobalt("https://co.wuk.sh/")
        when (val response = client.request(parseResult.url)) {
            is RedirectResponse -> redirect(response)
            is StreamResponse -> stream(response, parseResult)
            else -> return
        }
    }
}