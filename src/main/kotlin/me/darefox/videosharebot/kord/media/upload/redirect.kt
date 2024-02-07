package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.videosharebot.kord.extensions.BotMessage

suspend fun uploadRedirect(response: RedirectResponse, userMessage: Message, botMessage: BotMessage) {
    botMessage.ref.edit {
        content = response.redirectUrl
    }
    userMessage.edit {
        suppressEmbeds = true
    }
}

