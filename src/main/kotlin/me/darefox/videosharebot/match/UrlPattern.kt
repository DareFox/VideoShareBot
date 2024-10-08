package me.darefox.videosharebot.match

data class UrlPattern(
    val baseDomains: List<String>,
    val subdomains: List<String> = listOf(""),
    val segmentPatterns: List<UrlSegmentPattern>
) {
    init {
        baseDomains.forEach {
            val dots = it.filter { char -> char == '.' }
            require(dots.isNotEmpty()) { "Base domain ($it) should have at least one dot "}
        }
        subdomains.forEach {
            require(!it.endsWith('.')) { "Subdomain ($it) shouldn't end on dot" }
        }
    }
}

data class UrlSegmentPattern(
    val queryMatcher: List<UrlQueryMatcher>,
    val segmentMatchers: List<UrlSegmentMatcher>,
)