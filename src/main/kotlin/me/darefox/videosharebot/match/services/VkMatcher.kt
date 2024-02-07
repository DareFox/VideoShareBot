package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*
import me.darefox.videosharebot.match.segments.CombinedMatcher
import me.darefox.videosharebot.match.segments.TextLength
import me.darefox.videosharebot.match.segments.TextStartsWith

object VkMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("vk.com"),
        subdomains = listOf(""),
        segmentMatchers = listOf(
            listOf(
                TextLength(23) and TextStartsWith("clip-")
            ),
            listOf(
                TextLength(24) and TextStartsWith("video")
            )
        )
    )
}
