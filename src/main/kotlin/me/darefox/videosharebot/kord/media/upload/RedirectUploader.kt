package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.Success
import dev.kord.core.behavior.edit
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.videosharebot.extensions.ResultMonad

data object RedirectUploader: Uploader<RedirectResponse>() {
    override suspend fun upload(context: UploadContext<RedirectResponse>): ResultMonad<Unit, String> {
        context.botMessageStatus.changeTo(context.cobaltResponse.redirectUrl)
        context.userMessage.edit {
            suppressEmbeds = true
        }
        return Success(Unit)
    }
}
