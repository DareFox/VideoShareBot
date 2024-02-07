package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

object Anything: UrlSegmentMatcher {
    override fun validate(text: String): Boolean = true
}