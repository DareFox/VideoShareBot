package me.darefox.videosharebot.match

data class UrlQueryMatcher(
    val key: String,
    val validator: UrlSegmentMatcher
)

fun UrlSegmentMatcher.asQueryMatcher(key: String) = UrlQueryMatcher(key, this)
