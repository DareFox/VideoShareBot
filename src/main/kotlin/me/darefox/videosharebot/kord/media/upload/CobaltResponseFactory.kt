package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.peekFailure
import me.darefox.cobaltik.wrapper.*
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.match.CompositeMatcherResult
import me.darefox.videosharebot.match.services.InstagramMatcher
import me.darefox.videosharebot.tools.stringtransformers.DoNothingWithString

@Suppress("UNCHECKED_CAST")
object CobaltResponseFactory {
    private val alwaysStream = mutableListOf(InstagramMatcher)
    suspend fun uploadMedia(
        parsedResult: CompositeMatcherResult,
        eventContext: UploadContext<WrappedCobaltResponse>
    ) {
        val result: ResultMonad<Unit, UploadError> =  when (eventContext.cobaltResponse) {
            is PickerResponse -> PickerUploader.upload(eventContext as UploadContext<PickerResponse>)
            is RedirectResponse -> {
                val context = eventContext as UploadContext<RedirectResponse>
                if (parsedResult.parser in alwaysStream) {
                    val asStream = StreamResponse(context.cobaltResponse.redirectUrl)
                    val newContext = eventContext.copy(cobaltResponse = asStream)
                    StreamUploader.upload(newContext as UploadContext<StreamResponse>)
                } else {
                    RedirectUploader.upload(context)
                }
            }
            is StreamResponse -> StreamUploader.upload(eventContext as UploadContext<StreamResponse>)
            is ErrorResponse -> {
                eventContext.botMessageStatus.cancel("Cobalt error: ${eventContext.cobaltResponse.text}")
                return
            }
            else -> {
                eventContext.botMessageStatus.cancel("${eventContext.cobaltResponse} is not supported")
                return
            }

        }

        result.peekFailure {
            eventContext.botMessageStatus.cancel(
                content = it.asDiscordMessageText,
                overrideTransformer = DoNothingWithString
            )
        }
    }
}