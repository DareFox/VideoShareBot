package me.darefox.videosharebot.extensions

import io.ktor.client.statement.*
import io.ktor.http.*
import me.darefox.videosharebot.http.MimeMap
import me.darefox.videosharebot.tools.FileExtension
import me.darefox.videosharebot.tools.Filename
import java.net.URL
import kotlin.math.min

fun HttpResponse.filename(): Filename? {
    val contentDispositionResult = headers["Content-Disposition"]?.let {
        val regex = Regex("(?<=filename=\").*(?=\")")

        val name = regex.find(it)?.value ?: return@let null
        val extension = name.split(".").getOrNull(1) ?: return null

        Filename(name, FileExtension(".$extension"))
    }
    val contentType = contentType()?.let { contentType ->
        val extension = MimeMap[contentType.contentType + "/" + contentType.contentSubtype]
            ?: contentType.fileExtensions().firstOrNull()
            ?: return@let null

        val url = this.request.url.toString()
        val maxLengthString = min(220, url.lastIndex)
        Filename(
            prefix = URL(url.slice(0..maxLengthString)).toSafeFilename(),
            extension = FileExtension(extension)
        )
    }
    return contentDispositionResult ?: contentType
}