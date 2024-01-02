package match.services

import match.*
import match.NumberLength

object TikTokMatcher: UrlMatcher() {
    override val pattern: UrlPattern = UrlPattern(
        baseDomains = listOf("tiktok.com"),
        subdomains = listOf("www", "vm", ""),
        segmentMatchers = listOf(
            listOf(
                Anything,
                SpecificText("video"),
                NumberLength(numberOfDigits = 19)
            ),
            listOf(
                TextLength(length = 9)
            )
        )
    )
}