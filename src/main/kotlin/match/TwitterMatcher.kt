package match

import enhancements.nestedListOf

object TwitterMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("twitter.com", "x.com"),
        segmentMatchers = nestedListOf(
            Anything,
            SpecificText("status"),
            Number(19)
        )
    )
}