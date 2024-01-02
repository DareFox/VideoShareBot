package match.services

import match.*
import match.Number

object TikTokMatcher: UrlMatcher() {
    override val pattern: UrlPattern = UrlPattern(
        baseDomains = listOf("tiktok.com"),
        subdomains = listOf("www", "vm", ""),
        segmentMatchers = listOf(
            listOf(
                Anything,
                SpecificText("video"),
                Number(length = 19)
            ),
            listOf(
                Text(length = 9)
            )
        )
    )
}