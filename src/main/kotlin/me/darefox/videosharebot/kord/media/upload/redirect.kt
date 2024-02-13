package me.darefox.videosharebot.kord.media.upload

import dev.kord.core.behavior.edit
import me.darefox.cobaltik.wrapper.RedirectResponse

suspend fun uploadRedirect(eventContext: UploadContext<RedirectResponse>) {
    eventContext.botMessageStatus.changeTo(eventContext.cobaltResponse.redirectUrl)
    eventContext.userMessage.edit {
        suppressEmbeds = true
    }
}

