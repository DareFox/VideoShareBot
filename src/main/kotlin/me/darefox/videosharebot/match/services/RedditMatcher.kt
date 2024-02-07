package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.segments.Anything
import me.darefox.videosharebot.match.segments.SpecificText
import me.darefox.videosharebot.match.segments.TextLength

object RedditMatcher: UrlMatcher() {
    private val withoutId = listOf(
        Anything,
        Anything,
        SpecificText("comments") or SpecificText("s"),
        TextLength(7) or TextLength(10)
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