package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.Success
import dev.kord.core.behavior.edit
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.kord.BotError

data object RedirectUploader: Uploader<RedirectResponse, UploadError>() {
    override suspend fun upload(context: UploadContext<RedirectResponse>): ResultMonad<Unit, UploadError> {
        context.botMessageStatus.changeTo(context.cobaltResponse.redirectUrl)
        context.userMessage.edit {
            suppressEmbeds = true
        }
        return Success(Unit)
    }
}
