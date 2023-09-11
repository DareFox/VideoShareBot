package parser

import enhancements.CheckMachine

object TikTokParser : ServiceUrlParser("TikTok") {
    override fun parse(text: String): List<String> {
        val list = anyUrlRegex.findAll(text).mapNotNull {
            urlCheck(it.value)
        }.toList()

        return list
    }

    private fun urlCheck(url: String): String? {
        val splitter = UrlSplitter(url)
        return when (splitter.host) {
            "vm.tiktok.com" -> shortLinkCheck(splitter)
            "tiktok.com" -> longLinkCheck(splitter)
            else -> {
                log.debug { "$url is invalid" }
                null
            }
        }
    }

    private fun shortLinkCheck(splitter: UrlSplitter): String? {
        val check = CheckMachine(
            "isIdGood" to (splitter.pathSplit.getOrNull(0)?.length == 9),
            "pathLength" to (splitter.pathSplit.size == 1),
            "noSlash" to !splitter.endsOnSlash
        )

        log.debug {
            """
            url = ${splitter.url},
            checks = $check,
            splitter = $splitter
        """.trimIndent()
        }
        return if (check.isAllTrue) splitter.url else null
    }

    private fun longLinkCheck(splitter: UrlSplitter): String? {
        val check = CheckMachine(
            "isVideo" to (splitter.pathSplit.getOrNull(1) == "video"),
            "isIdGood" to (splitter.pathSplit.getOrNull(2)?.length == 19),
            "pathLength" to (splitter.pathSplit.size == 3),
            "noSlash" to !splitter.endsOnSlash
        )

        log.debug {
            """
            url = ${splitter.url},
            checks = $check,
            splitter = $splitter
        """.trimIndent()
        }
        return if (check.isAllTrue) splitter.url else null
    }
}