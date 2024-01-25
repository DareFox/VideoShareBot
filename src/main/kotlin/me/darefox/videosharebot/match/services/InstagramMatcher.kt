package me.darefox.videosharebot.match.services

import me.darefox.videosharebot.extensions.nestedListOf
import me.darefox.videosharebot.match.SpecificText
import me.darefox.videosharebot.match.TextLength
import me.darefox.videosharebot.match.UrlMatcher
import me.darefox.videosharebot.match.UrlPattern

object InstagramMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("instagram.com"),
        listOf("www", ""),
        segmentMatchers = nestedListOf(
            SpecificText("reel"),
            TextLength(11)
        )
    )
}