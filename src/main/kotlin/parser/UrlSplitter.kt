package parser

class UrlSplitter(val url: String) {
    val scheme: String?
    val host: String
    val pathSplitted: List<String>
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
        pathSplitted = if (hostPathSplit.size == 1) {
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
        return "UrlSplitter(url='$url', scheme=$scheme, host='$host', pathSplitted=$pathSplitted, endsOnSlash=$endsOnSlash)"
    }
}