package http

import extensions.toSafeFilename
import io.ktor.utils.io.errors.*
import org.apache.commons.io.input.CountingInputStream
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import java.net.URL

data class StreamFile(
    val response: Response,
    val filename: String,
    val size: Long?,
    val stream: CountingInputStream
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

    return StreamFile(
        response = response,
        filename = filename,
        size = response.header("Content-Length")?.toLongOrNull(),
        stream = CountingInputStream(response.body.stream)
    )
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