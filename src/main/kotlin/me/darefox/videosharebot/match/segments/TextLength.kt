package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class TextLength(val length: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length == length
}