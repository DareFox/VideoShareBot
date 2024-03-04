package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.segments.TextLength
import me.darefox.videosharebot.match.segments.TextStartsWith

object VkMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("vk.com"),
        subdomains = listOf(""),
        segmentPatterns = listOf(
            UrlSegmentPattern(
                queryMatcher = listOf(),
                segmentMatchers = listOf(
                    TextLength(23) and TextStartsWith("clip-")
                )
            ),
            UrlSegmentPattern(
                queryMatcher = listOf(),
                segmentMatchers = listOf(
                    TextLength(24) and TextStartsWith("video")
                )
            )
        )
    )
}
