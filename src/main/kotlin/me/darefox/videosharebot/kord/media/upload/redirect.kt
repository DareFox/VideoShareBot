package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.behavior.edit
import me.darefox.cobaltik.wrapper.RedirectResponse

suspend fun uploadRedirect(eventContext: UploadContext<RedirectResponse>) {
    eventContext.botMessage.ref.edit {
        content = eventContext.cobaltResponse.redirectUrl
    }
    eventContext.userMessage.edit {
        suppressEmbeds = true
    }
}

