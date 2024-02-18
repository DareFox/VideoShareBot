package me.darefox.videosharebot.kord.media.upload

import me.darefox.cobaltik.wrapper.PickerResponse
import me.darefox.cobaltik.wrapper.RedirectResponse
import me.darefox.cobaltik.wrapper.StreamResponse
import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.match.CompositeMatcherResult
import me.darefox.videosharebot.match.services.InstagramMatcher

@Suppress("UNCHECKED_CAST")
object CobaltResponseFactory {
    private val alwaysStream = mutableListOf(InstagramMatcher)
    suspend fun uploadMedia(
        parsedResult: CompositeMatcherResult,
        eventContext: UploadContext<WrappedCobaltResponse>
    ) {
        when (eventContext.cobaltResponse) {
            is PickerResponse -> PickerUploader.upload(eventContext as UploadContext<PickerResponse>)
            is RedirectResponse -> {
                val context = eventContext as UploadContext<RedirectResponse>
                if (parsedResult.parser in alwaysStream) {
                    val asStream = StreamResponse(context.cobaltResponse.redirectUrl)
                    val newContext = eventContext.copy(cobaltResponse = asStream)
                    this.uploadMedia(parsedResult, newContext)
                } else {
                    RedirectUploader.upload(context)
                }
            }
            is StreamResponse -> StreamUploader.upload(eventContext as UploadContext<StreamResponse>)
            else -> eventContext.botMessageStatus.changeTo("${eventContext.cobaltResponse} is not supported")
        }
    }


}