package me.darefox.videosharebot.kord.listeners

import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.respond
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.*
import me.darefox.videosharebot.extensions.asInlineCode
import me.darefox.videosharebot.extensions.tryAsResult
import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.services.*
import me.darefox.cobaltik.wrapper.*
import me.darefox.videosharebot.kord.extensions.BotMessageStatus
import me.darefox.videosharebot.kord.extensions.changeToExceptionError
import me.darefox.videosharebot.kord.media.upload.CobaltResponseFactory
import me.darefox.videosharebot.match.CompositeMatcher

class MessageListener : LoggerExtension("MessageListener") {
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
        val url = parsedUrls.first()

        val botMessage = event.message.respond(
            asInlineCode("Trying to find video..."),
            pingInReply = false,
            useReply = true
        )

        val botMessageStatus = BotMessageStatus(botMessage, scope)

        log.info { "Trying to ask cobalt for $url" }
        val client = Cobalt("https://co.wuk.sh/")
        val response = tryAsResult<WrappedCobaltResponse, Exception> {
            client.request(url.url) { removeTikTokWatermark = true }
        }

        when (response) {
            is Failure -> botMessage.changeToExceptionError(response.reason)
            is Success -> CobaltResponseFactory.uploadMedia(url, response.value, botMessage, botMessageStatus)
        }

    }
}
