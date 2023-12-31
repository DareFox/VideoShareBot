package match.services

import extensions.nestedListOf
import match.*
import match.NumberLength

object TwitterMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("twitter.com", "x.com"),
        segmentMatchers = nestedListOf(
            Anything,
            SpecificText("status"),
            NumberLength(19)
        )
    )
}