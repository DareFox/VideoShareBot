package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.extensions.nestedListOf
import me.darefox.videosharebot.match.*

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
