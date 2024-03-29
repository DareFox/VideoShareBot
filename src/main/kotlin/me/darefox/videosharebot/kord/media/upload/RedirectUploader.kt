package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.Success
import dev.kord.core.behavior.edit
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.kord.BotError
import me.darefox.videosharebot.tools.stringtransformers.DoNothingWithString

data object RedirectUploader: Uploader<RedirectResponse, UploadError>() {
    override suspend fun upload(context: UploadContext<RedirectResponse>): ResultMonad<Unit, UploadError> {
        context.botMessageStatus.cancel(context.cobaltResponse.redirectUrl, DoNothingWithString)
        context.userMessage.edit {
            suppressEmbeds = true
        }
        return Success(Unit)
    }
}
