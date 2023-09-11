package parser

/**
 * A utility class for splitting and analyzing URLs into their constituent parts such as scheme, host, and path.
 *
 * @param url The URL string to be split and analyzed.
 * @property scheme The scheme (e.g., "http", "https") part of the URL, or null if not present.
 * @property host The host part of the URL, typically representing the domain.
 * @property pathSplit A list of path segments extracted from the URL, excluding the scheme and host.
 * @property endsOnSlash Indicates whether the URL ends with a forward slash ('/').
 *
 * @throws IllegalArgumentException if the URL is empty or improperly formatted.
 */
class UrlSplitter(val url: String) {
    val scheme: String?
    val host: String
    val pathSplit: List<String>
    val endsOnSlash: Boolean

    init {
        val trimmedUrl = url.trim()
        endsOnSlash = trimmedUrl.last() == '/'

        val schemeSplit = trimmedUrl.split("://")
        val afterScheme: String

        scheme = when (schemeSplit.size) {
            1 -> {
                afterScheme = schemeSplit[0]
                null
            }
            2 -> {
                afterScheme = schemeSplit[1]
                schemeSplit[0]
            }
            else -> throw IllegalArgumentException("Url is empty")
        }

        val hostPathSplit = afterScheme.split("/")
        host = hostPathSplit[0]
        pathSplit = if (hostPathSplit.size == 1) {
            listOf()
        } else {
            val lastIndex = if (endsOnSlash) {
                hostPathSplit.lastIndex
            } else {
                hostPathSplit.lastIndex + 1
            }
            hostPathSplit.subList(1, lastIndex)
        }
    }

    override fun toString(): String {
        return "UrlSplitter(url='$url', scheme=$scheme, host='$host', pathSplit=$pathSplit, endsOnSlash=$endsOnSlash)"
    }
}