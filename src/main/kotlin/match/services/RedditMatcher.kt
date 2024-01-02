package match.services

import match.*

object RedditMatcher: UrlMatcher() {
    private val withoutId = listOf(
        Anything,
        Anything,
        SpecificText("comments"),
        TextLength(7)
    )

    override val pattern = UrlPattern(
        baseDomains = listOf("reddit.com"),
        subdomains = listOf("", "www"),
        segmentMatchers = listOf(
            withoutId,
            withoutId + Anything
        )
    )
}