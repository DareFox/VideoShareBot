package me.darefox.videosharebot.kord.upload

import dev.forkhandles.result4k.peekFailure
import me.darefox.cobaltik.wrapper.*
import me.darefox.videosharebot.config.GlobalApplicationConfig
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.match.CompositeMatcherResult
import me.darefox.videosharebot.tools.stringtransformers.DoNothingWithString
import me.darefox.videosharebot.tools.toValues

@Suppress("UNCHECKED_CAST")
object CobaltResponseFactory {
    private val cobaltConfig = GlobalApplicationConfig.cobalt
    private val alwaysStream = cobaltConfig.alwaysDownload.toValues()
    suspend fun uploadMedia(
        parsedResult: CompositeMatcherResult,
        eventContext: UploadContext<WrappedCobaltResponse>
    ) {
        val result: ResultMonad<Unit, UploadFault> =  when (eventContext.cobaltResponse) {
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
                content = it.message,
                overrideTransformer = DoNothingWithString
            )
        }
    }
}