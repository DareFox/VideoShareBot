package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.entity.Message
import me.darefox.cobaltik.wrapper.PickerResponse
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.cobaltik.wrapper.StreamResponse
import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.kord.extensions.BotMessageStatus
import me.darefox.videosharebot.match.CompositeMatcherResult
import me.darefox.videosharebot.match.services.InstagramMatcher

object CobaltResponseFactory {
    private val alwaysStream = mutableListOf(InstagramMatcher)
    suspend fun uploadMedia(
        parsedResult: CompositeMatcherResult,
        response: WrappedCobaltResponse,
        userMessage: Message,
        botMessage: Message,
        botMessageStatus: BotMessageStatus
    ) {
        when (response) {
            is PickerResponse -> uploadPicker(
                response = response,
                userMessage = userMessage,
                botMessage = botMessage,
                botMessageStatus = botMessageStatus
            )
            is RedirectResponse -> {
                if (parsedResult.parser in alwaysStream) {
                    val asStream = StreamResponse(response.redirectUrl)
                    this.uploadMedia(parsedResult, asStream, userMessage, botMessage, botMessageStatus)
                } else {
                    uploadRedirect(
                        response = response,
                        userMessage = userMessage,
                        botMessage = botMessage
                    )
                }
            }
            is StreamResponse -> uploadStream(
                response = response,
                userMessage = userMessage,
                botMessage = botMessage,
                botMessageStatus = botMessageStatus
            )
            else -> botMessageStatus.status == "$response is not supported"
        }
    }


}