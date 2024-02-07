package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*

object VkMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        baseDomains = listOf("vk.com"),
        subdomains = listOf(""),
        segmentMatchers = listOf(
            listOf(
                CombinedMatcher(
                    TextLength(23),
                    TextStartsWith("clip-")
                )
            ),
            listOf(
                CombinedMatcher(
                    TextLength(24),
                    TextStartsWith("video")
                )
            )
        )
    )
}
