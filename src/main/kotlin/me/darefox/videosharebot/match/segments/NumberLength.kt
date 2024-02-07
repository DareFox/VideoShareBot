package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class NumberLength(val numberOfDigits: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        text.toLongOrNull() ?: return false
        return text.length == numberOfDigits
    }
}