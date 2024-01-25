package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*

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