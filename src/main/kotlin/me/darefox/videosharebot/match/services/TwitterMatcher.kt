package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.segments.Anything
import me.darefox.videosharebot.match.segments.NumberLength
import me.darefox.videosharebot.match.segments.SpecificText

object TwitterMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("twitter.com", "x.com"),
        segmentPatterns = listOf(
            UrlSegmentPattern(
                queryMatcher = listOf(),
                segmentMatchers = listOf(
                    Anything,
                    SpecificText("status"),
                    NumberLength(19)
                )
            )
        )
    )
}
