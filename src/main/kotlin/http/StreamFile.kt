package http

import MimeMap
import extensions.toSafeFilename
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import io.ktor.utils.io.jvm.javaio.*
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.stream.Collectors
import kotlin.streams.toList

data class StreamFile(
    val response: Response,
    val filename: String,
    val stream: ByteReadChannel
)

fun streamInternetFile(getUrl: String): StreamFile = streamInternetFile(Request(Method.GET, getUrl))

fun streamInternetFile(request: Request): StreamFile {
    val response = HttpStreamingClient(request)

    val contentDisposition = response.header("Content-Disposition")
    val contentType = response.header("Content-Type")
    val filename = when {
        contentDisposition != null -> contentDespositionFilename(contentDisposition)
        contentType != null -> contentTypeFilename(contentType, request.uri.toString())
        else -> throw IOException("No candidates for filename")
    }

    return StreamFile(response, filename, response.body.stream.toByteReadChannel())
}

private fun contentDespositionFilename(contentDisposition: String): String {
    val regex = Regex("(?<=filename=\").*(?=\")")
    val filename = regex.find(contentDisposition)?.value ?: throw IOException("Wrong Content-Disposition value")
    return filename
}

private fun contentTypeFilename(contentType: String, url: String): String {
    val extension = MimeMap[contentType] ?: throw IllegalStateException("$contentType is not supported media")
    val baseFilename = URL(url).toSafeFilename()
    return baseFilename + extension
}