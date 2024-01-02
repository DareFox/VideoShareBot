package match

import enhancements.nestedListOf
import io.ktor.http.*

object InstagramMatcher: UrlMatcher() {
    override val pattern = UrlPattern(
        listOf("instagram.com"),
        listOf("www", ""),
        segmentMatchers = nestedListOf(
            SpecificText("reel"),
            Text(11)
        )
    )
}