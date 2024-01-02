package match.services

import enhancements.nestedListOf
import match.SpecificText
import match.TextLength
import match.UrlMatcher
import match.UrlPattern

object YoutubeShortsMatcher : UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("youtube.com"),
        subdomains = listOf("", "www"),
        segmentMatchers = nestedListOf(
            SpecificText(shouldBe = "shorts"),
            TextLength(length = 11)
        )
    )
}



