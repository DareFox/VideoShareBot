package match

import enhancements.nestedListOf

object YoutubeShortsMatcher : UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("youtube.com"),
        segmentMatchers = nestedListOf(
            SpecificText(shouldBe = "shorts"),
            Text(length = 11)
        )
    )
}



