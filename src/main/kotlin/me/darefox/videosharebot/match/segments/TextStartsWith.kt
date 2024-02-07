package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class TextStartsWith(val prefix: String, val ignoreCase: Boolean = false):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.startsWith(prefix, ignoreCase)
}