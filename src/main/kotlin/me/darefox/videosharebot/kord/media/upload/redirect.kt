package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import me.darefox.cobaltik.wrapper.RedirectResponse

suspend fun uploadRedirect(response: RedirectResponse, userMessage: Message, botMessage: Message) {
    botMessage.edit {
        content = response.redirectUrl
    }
    userMessage.edit {
        suppressEmbeds = true
    }
}

