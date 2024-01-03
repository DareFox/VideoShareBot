package listeners

import MimeMap
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import com.sun.nio.sctp.IllegalReceiveException
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import extensions.toSafeFilename
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.util.*
import ktor
import match.*
import match.services.*
import me.darefox.cobaltik.models.PickerType
import me.darefox.cobaltik.wrapper.Cobalt
import me.darefox.cobaltik.wrapper.PickerResponse
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
            VkMatcher,
            InstagramMatcher
        )
    )

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                actionImpl()
            }
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.stream(streamResponse: StreamResponse, parseResult: CompositeMatcherResult) {
        val pair = downloadMedia(streamResponse.streamUrl)
        event.message.respond {
            addFile(pair.first, pair.second)
        }
        event.message.edit {
            suppressEmbeds = true
        }
    }

    private suspend fun downloadMedia(url: String): Pair<String,ChannelProvider> {
        val request = ktor.get(url)
        val contentType = request.headers["Content-Type"] ?: "video/mp4"
        val extension =
            MimeMap[contentType] ?: throw IllegalReceiveException("Content-type $contentType is not supported")
        val filename = URL(url).toSafeFilename() + extension
        val channel = request.bodyAsChannel()

        return filename to ChannelProvider { channel }
    }

    private suspend fun EventContext<MessageCreateEvent>.redirect(redirectResponse: RedirectResponse) {
        event.message.respond(redirectResponse.redirectUrl)
        event.message.edit {
            suppressEmbeds = true
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.picker(response: PickerResponse, parseResult: CompositeMatcherResult) {
        when (response.type) {
            PickerType.VARIOUS -> log.error { "Various picker is not supported: $response" }
            PickerType.IMAGES -> {
                val chunks = response.items.chunked(10)

                for (chunk in chunks) {
                    event.message.respond {
                        for (image in chunk) {
                            try {
                                val pair = downloadMedia(image.url)
                                addFile(pair.first, pair.second)
                            } catch (e: Throwable) {
                                log.error(e) {"error during downloading media"}
                            }
                        }
                    }
                }

            }
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
            is RedirectResponse -> {
                if (parseResult.parser is InstagramMatcher) {
                    // Instagram doesn't play in discord when redirecting link
                    stream(StreamResponse(response.redirectUrl), parseResult)
                } else {
                    redirect(response)
                }
            }
            is StreamResponse -> stream(response, parseResult)
            is PickerResponse -> picker(response, parseResult)
            else -> log.info { response }
        }
    }


}