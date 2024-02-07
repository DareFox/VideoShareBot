package me.darefox.videosharebot.match.segments

import me.darefox.videosharebot.match.UrlSegmentMatcher

class SpecificText(val shouldBe: String, val ignoreCase: Boolean = false):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.equals(shouldBe, false)
}