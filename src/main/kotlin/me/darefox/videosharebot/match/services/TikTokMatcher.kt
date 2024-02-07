package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.segments.Anything
import me.darefox.videosharebot.match.segments.NumberLength
import me.darefox.videosharebot.match.segments.SpecificText
import me.darefox.videosharebot.match.segments.TextLength

object TikTokMatcher: UrlMatcher() {
    override val pattern: UrlPattern = UrlPattern(
        baseDomains = listOf("tiktok.com"),
        subdomains = listOf("www", "vm", ""),
        segmentMatchers = listOf(
            listOf(
               Anything,
               SpecificText("video"),
                NumberLength(numberOfDigits = 19)
            ),
            listOf(
                TextLength(length = 9)
            )
        )
    )
}