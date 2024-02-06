package me.darefox.videosharebot.extensions

import io.ktor.client.statement.*
import io.ktor.http.*
import me.darefox.videosharebot.http.MimeMap
import java.net.URL

fun HttpResponse.filename(): String? {
    val contentDispositionResult = headers["Content-Disposition"]?.let {
        val regex = Regex("(?<=filename=\").*(?=\")")
        regex.find(it)?.value
    }
    val contentType = contentType()?.let { contentType ->
        val extension = MimeMap[contentType.contentType + "/" + contentType.contentSubtype]
            ?: contentType.fileExtensions().firstOrNull()
            ?: return@let null
        URL(this.request.url.toString()).toSafeFilename() + ".$extension"
    }
    return contentDispositionResult ?: contentType
}