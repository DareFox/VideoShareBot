package listeners

import MimeMap
import co.touchlab.stately.concurrency.AtomicInt
import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import com.sun.nio.sctp.IllegalReceiveException
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import extensions.sendErrorAsEmbed
import extensions.toSafeFilename
import extensions.toSingleCodeLineMarkdown
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.utils.io.errors.*
import ktor
import match.*
import match.services.*
import me.darefox.cobaltik.models.PickerItem
import me.darefox.cobaltik.models.PickerType
import me.darefox.cobaltik.wrapper.*
import java.net.URL

class MessageListener : LoggerExtension("MessageListener") {
    private val stopSignEmoji = "\uD83D\uDED1"

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

    private suspend fun EventContext<MessageCreateEvent>.actionImpl() {
        if (event.member?.isBot == true) return
        val shareUrl = parser.parse(event.message.content)

        if (shareUrl.isEmpty()) return
        val parseResult = shareUrl.first()

        val botMessage = event.message.respond(
            "Trying to downloaded it...".toSingleCodeLineMarkdown(),
            pingInReply = false,
            useReply = true
        )

        log.info { "Trying to ask cobalt for $parseResult" }
        val client = Cobalt("https://co.wuk.sh/")
        val response = client.request(parseResult.url) {
            removeTikTokWatermark = true
        }

        when {
            response is RedirectResponse && parseResult.parser is InstagramMatcher -> {
                stream(StreamResponse(response.redirectUrl), parseResult, botMessage)
            }
            response is RedirectResponse -> redirect(response, botMessage)
            response is StreamResponse -> stream(response, parseResult, botMessage)
            response is PickerResponse -> picker(response, parseResult, botMessage)
            response is ErrorResponse -> {
                botMessage.edit {
                    val text = response.text ?: "Unknown error"
                    content = (stopSignEmoji + text).toSingleCodeLineMarkdown()
                }
                log.error { "${parseResult.url} ERR:" + response.text }
            }

            response is RateLimitResponse ->{
                botMessage.edit {
                    content = "$stopSignEmoji Too much requests. Try again later"
                }
            }
            else -> return
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

    private suspend fun EventContext<MessageCreateEvent>.stream(
        streamResponse: StreamResponse,
        parseResult: CompositeMatcherResult,
        botMessage: Message
    ) {
        val pair = try {
            downloadMedia(streamResponse.streamUrl)
        } catch (e: IOException) {
            botMessage.delete("Error")
            event.message.sendErrorAsEmbed(e)
            return
        }

        botMessage.edit {
            content = null
            addFile(pair.first, pair.second)
        }
        event.message.edit {
            suppressEmbeds = true
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.redirect(
        redirectResponse: RedirectResponse,
        botMessage: Message
    ) {
        botMessage.edit {
            content = redirectResponse.redirectUrl
        }
        event.message.edit {
            suppressEmbeds = true
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.picker(
        response: PickerResponse,
        parseResult: CompositeMatcherResult,
        botMessage: Message
    ) {
        when (response.type) {
            PickerType.VARIOUS -> {
                botMessage.edit {
                    content = "$stopSignEmoji This type is not supported by bot"
                }
            }
            PickerType.IMAGES -> {
                pickerImages(response, botMessage)
            }
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.pickerImages(
        response: PickerResponse,
        botMessage: Message,
    ) {
        val chunks = response.items.chunked(10)
        val errorCounter = AtomicInt(0)
        
        for ((index, chunk) in chunks.withIndex()) {
            if (index == 0) {
                botMessage.edit {
                    content = null
                    processImageChunk(chunk, errorCounter, botMessage) { name, file ->
                        addFile(name, file)
                    }
                }
            } else {
                event.message.respond(pingInReply = false, useReply = true) {
                    processImageChunk(chunk, errorCounter, botMessage) { name, file ->
                        addFile(name, file)
                    }
                }
            }
        }

        if (errorCounter.get() == response.items.size) {
            botMessage.edit {
                "$stopSignEmoji Couldn't download any media"
            }
        }
    }

    private suspend fun processImageChunk(
        chunk: List<PickerItem>,
        errorCounter: AtomicInt,
        botMessage: Message,
        addFile: (String, ChannelProvider) -> Unit
    ) {
        for (image in chunk) {
            try {
                val pair = downloadMedia(image.url)
                addFile(pair.first, pair.second)
            } catch (e: Throwable) {
                log.error(e) { "error during downloading media" }
                if (errorCounter.get() == 0) botMessage.edit {
                    content = "$stopSignEmoji I couldn't download all media"
                }
                errorCounter.incrementAndGet()
            }
        }
    }
}