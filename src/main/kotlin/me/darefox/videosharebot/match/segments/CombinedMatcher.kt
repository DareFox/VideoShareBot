package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class CombinedMatcher(vararg val matchers: UrlSegmentMatcher):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        return matchers.all { it.validate(text) }
    }
}