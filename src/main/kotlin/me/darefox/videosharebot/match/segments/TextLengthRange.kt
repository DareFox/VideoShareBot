package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class TextLengthRange(val lengthRange: IntRange): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length in lengthRange
}