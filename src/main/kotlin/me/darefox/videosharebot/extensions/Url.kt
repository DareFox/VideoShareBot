package me.darefox.videosharebot.extensions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.MalformedURLException
import java.net.URL

/**
 * Attempts to parse this string as a URL.
 *
 * @return A [URL] object if the parsing is successful, or null if the string is not a valid URL.
 */
fun String.tryParseURL(): URL? {
    return try {
        URL(this)
    } catch (_: MalformedURLException) {
        null
    }
}

/**
 * Extension function to clean and convert a URL into a safe filename format.
 *
 * This function takes a URL and processes its host and file components to generate
 * a sanitized string that can be used as a filename.
 *
 * @receiver The URL to be cleaned and converted.
 * @return A sanitized string suitable for use as a filename.
 */
fun URL.toSafeFilename(): String {
    val regex = """\W+""".toRegex()
    val underscoreRegex = """_{3,}""".toRegex()
    val toConvert = host + file

    return toConvert
        .replace(regex, "_")
        .replace(underscoreRegex, "__")
}

object URLSerializer: KSerializer<URL> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(this.javaClass.name, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URL {
        return URL(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: URL) {
        encoder.encodeString(value.toString())
    }
}