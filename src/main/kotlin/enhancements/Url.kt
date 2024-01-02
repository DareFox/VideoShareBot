package enhancements

import java.net.MalformedURLException
import java.net.URL
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