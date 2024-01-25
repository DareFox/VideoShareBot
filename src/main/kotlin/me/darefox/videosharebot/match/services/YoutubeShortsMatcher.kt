package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.extensions.nestedListOf
import me.darefox.videosharebot.match.SpecificText
import me.darefox.videosharebot.match.TextLength
import me.darefox.videosharebot.match.UrlMatcher
import me.darefox.videosharebot.match.UrlPattern

object YoutubeShortsMatcher : UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("youtube.com"),
        subdomains = listOf("", "www"),
        segmentMatchers = nestedListOf(
            SpecificText(shouldBe = "shorts"),
            TextLength(length = 11)
        )
    )
}



