package match.services

import extensions.nestedListOf
import match.SpecificText
import match.TextLength
import match.UrlMatcher
import match.UrlPattern

object InstagramMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("instagram.com"),
        listOf("www", ""),
        segmentMatchers = nestedListOf(
            SpecificText("reel"),
            TextLength(11)
        )
    )
}