package me.darefox.videosharebot.kord.listeners

import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.*
import me.darefox.cobaltik.wrapper.Cobalt
import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.extensions.createLogger
import me.darefox.videosharebot.extensions.tryAsResult
import me.darefox.videosharebot.kord.tools.BotMessageStatus
import me.darefox.videosharebot.kord.extensions.asBotMessage
import me.darefox.videosharebot.kord.extensions.changeToExceptionError
import me.darefox.videosharebot.kord.media.upload.CobaltResponseFactory
import me.darefox.videosharebot.kord.media.upload.UploadContext
import me.darefox.videosharebot.match.CompositeMatcher
import me.darefox.videosharebot.match.services.*
import me.darefox.videosharebot.tools.stringtransformers.MarkdownCodeInline
class MessageListener : Extension() {
    override val name: String = this::class.simpleName!!
    private val log = createLogger()

    val compositeParser = CompositeMatcher(
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
                val scope = CoroutineScope(Dispatchers.IO + CoroutineName("$name-Scope"))
                try {
                    scope.launch { actionImpl(scope) }.join()
                } finally {
                    scope.cancel()
                }
            }
        }
    }
    private suspend fun EventContext<MessageCreateEvent>.actionImpl(scope: CoroutineScope) {
        if (event.member?.isBot == true) return
        val parsedUrls = compositeParser.parse(event.message.content)

        if (parsedUrls.isEmpty()) return
        val originalUrl = parsedUrls.first()

        val defaultTransformer = MarkdownCodeInline
        val botMessage = event.message.respond(
            defaultTransformer("Trying to find media..."),
            pingInReply = false,
            useReply = true
        ).asBotMessage()

        val botMessageStatus = BotMessageStatus(botMessage, scope, defaultTransformer)

        log.info { "Trying to ask cobalt for $originalUrl" }
        val client = Cobalt("https://co.wuk.sh/")
        val response = tryAsResult<WrappedCobaltResponse, Exception> {
            client.request(originalUrl.url) { removeTikTokWatermark = true }
        }

        when (response) {
            is Failure -> botMessage.changeToExceptionError(response.reason)
            is Success -> CobaltResponseFactory.uploadMedia(originalUrl, UploadContext(
                userMessage = event.message,
                botMessage = botMessage,
                botMessageStatus = botMessageStatus,
                cobaltResponse = response.value
            ))
        }
    }
}
