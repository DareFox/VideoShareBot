package me.darefox.videosharebot.kord.upload

import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.extensions.ResultMonad

sealed class Uploader<T : WrappedCobaltResponse> {
    abstract suspend fun upload(context: UploadContext<T>): ResultMonad<Unit, UploadFault>
}