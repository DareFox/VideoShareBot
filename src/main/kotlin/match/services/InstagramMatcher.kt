package match.services

import enhancements.nestedListOf
import match.SpecificText
import match.Text
import match.UrlMatcher
import match.UrlPattern

object InstagramMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("instagram.com"),
        listOf("www", ""),
        segmentMatchers = nestedListOf(
            SpecificText("reel"),
            Text(11)
        )
    )
}