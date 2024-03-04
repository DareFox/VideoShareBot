package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.UrlMatcher
import me.darefox.videosharebot.match.UrlPattern
import me.darefox.videosharebot.match.UrlQueryMatcher
import me.darefox.videosharebot.match.UrlSegmentPattern
import me.darefox.videosharebot.match.segments.SpecificText
import me.darefox.videosharebot.match.segments.TextLength

object YoutubeMatcher : UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("youtube.com"),
        subdomains = listOf("", "www"),
        segmentPatterns = listOf(
            UrlSegmentPattern(
                queryMatcher = listOf(
                    UrlQueryMatcher("v", TextLength(11))
                ),
                segmentMatchers = listOf(
                    SpecificText("watch")
                )
            )
        )
    )
}

