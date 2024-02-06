package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.entity.Message
import me.darefox.cobaltik.wrapper.PickerResponse
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.cobaltik.wrapper.StreamResponse
import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.kord.extensions.BotMessageStatus
import me.darefox.videosharebot.match.CompositeMatcherResult


object CobaltResponseFactory {
    suspend fun uploadMedia(
        parsedResult: CompositeMatcherResult,
        response: WrappedCobaltResponse,
        botMessage: Message,
        botMessageStatus: BotMessageStatus
    ) {
        when (response) {
            is PickerResponse -> uploadPicker(response, botMessage, botMessageStatus)
            is RedirectResponse -> uploadRedirect(response, botMessage)
            is StreamResponse -> uploadStream(response, botMessage, botMessageStatus)
            else -> botMessageStatus.status == "$response is not supported"
        }
    }


}