package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.segments.SpecificText
import me.darefox.videosharebot.match.segments.TextLength
import me.darefox.videosharebot.match.UrlMatcher
import me.darefox.videosharebot.match.UrlPattern
import me.darefox.videosharebot.match.UrlSegmentPattern

object InstagramMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("instagram.com"),
        listOf("www", ""),
        segmentPatterns = listOf(
            UrlSegmentPattern(
                queryMatcher = listOf(),
                segmentMatchers = listOf(
                    SpecificText("reel"),
                    TextLength(11)
                )
            )
        )
    )
}