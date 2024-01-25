package me.darefox.videosharebot.http

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.extensions.fold
import me.darefox.videosharebot.extensions.toSafeFilename
import me.darefox.videosharebot.extensions.tryAsResult
import org.apache.commons.io.input.CountingInputStream
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import java.io.IOException
import java.net.URL

data class StreamFile(
    val response: Response,
    val filename: String,
    val size: Long?,
    val stream: CountingInputStream
)

fun streamInternetFile(getUrl: String): ResultMonad<StreamFile, Exception> = streamInternetFile(Request(Method.GET, getUrl))

fun streamInternetFile(request: Request): ResultMonad<StreamFile, Exception> {
    val response = tryAsResult<Response, IOException> { HttpStreamingClient(request) }
        .fold(
            ifSuccess = { value },
            ifFailure = { return this }
        )

    val contentDisposition = response.header("Content-Disposition")
    val contentType = response.header("Content-Type")
    val filename = when {
        contentDisposition != null -> contentDespositionFilename(contentDisposition)
        contentType != null -> contentTypeFilename(contentType, request.uri.toString())
        else -> return Failure(IOException("No candidates for filename"))
    }.fold(
        ifSuccess = { value },
        ifFailure = { return Failure(IOException(reason)) }
    )


    return Success(
        StreamFile(
        response = response,
        filename = filename,
        size = response.header("Content-Length")?.toLongOrNull(),
        stream = CountingInputStream(response.body.stream)
    )
    )
}

private fun contentDespositionFilename(contentDisposition: String): ResultMonad<String, String> {
    val regex = Regex("(?<=filename=\").*(?=\")")
    val filename = regex.find(contentDisposition)?.value
    return filename?.let { Success(it) } ?: Failure("Wrong Content-Disposition value")
}

private fun contentTypeFilename(contentType: String, url: String): ResultMonad<String, String> {
    val extension = MimeMap[contentType] ?: return Failure("$contentType is not supported media")
    val baseFilename = URL(url).toSafeFilename()
    return Success(baseFilename + extension)
}