package parser

import enhancements.CheckMachine

object YoutubeShortsParser : ServiceUrlParser("YoutubeShorts") {
    override fun parse(text: String): List<String> {
        return anyUrlRegex.findAll(text).mapNotNull {
            checkUrl(UrlSplitter(it.value))
        }.toList()
    }

    private fun checkUrl(urlSplitter: UrlSplitter): String? {
        val check = CheckMachine(
            "isYoutube" to (urlSplitter.host.endsWith("youtube.com")),
            "validPathLength" to (urlSplitter.pathSplit.size == 2),
            "isShorts" to (urlSplitter.pathSplit.getOrNull(0) == "shorts"),
            "validIdLength" to (urlSplitter.pathSplit.getOrNull(1)?.length == 11),
            "noSlash" to (!urlSplitter.endsOnSlash)
        )

        return if (check.isAllTrue) urlSplitter.url else null
    }
}



