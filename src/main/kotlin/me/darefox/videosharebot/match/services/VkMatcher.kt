package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.match.*

object VkMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("vk.com"),
        listOf(""),
        listOf(
            listOf(
                CombinedMatcher(
                    TextLength(24),
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
