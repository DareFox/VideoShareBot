package me.darefox.videosharebot.match.services

import match.*
import me.darefox.videosharebot.match.*

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