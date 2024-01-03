package match

/**
 * A composite matcher that aggregates multiple service-specific matchers to parse URLs from a given text.
 *
 *
 * @constructor Creates a [CompositeMatcher] with a set of provided [parsers].
 * @param parsers A set of [UrlMatcher] instances responsible for parsing URLs from text.
 *
 * @see UrlMatcher
 * @see CompositeMatcherResult
 */
class CompositeMatcher(private val parsers: Set<UrlMatcher>) {

    /**
     * Parses the given text using each of the aggregated [parsers] and returns a consolidated list of URLs.
     *
     * This method orchestrates the parsing process by delegating to each individual parser and collecting
     * the results into a single list. It ensures that URLs parsed by any of the service-specific matchers
     * are included in the final output.
     *
     * @param string The text to be parsed for URLs.
     * @return A list of [CompositeMatcherResult] objects, each representing a parsed URL and the parser that identified it.
     */
    fun parse(string: String): List<CompositeMatcherResult> {
        val urlList = mutableListOf<CompositeMatcherResult>()

        for (parser in parsers) {
            urlList += parser.parse(string).map {
                CompositeMatcherResult(it, parser)
            }
        }

        return urlList
    }
}

/**
 * Represents a parsed URL along with the specific [UrlMatcher] that parsed it.
 *
 * This data class encapsulates the parsed URL and its associated parser, providing context about
 * the source of the parsed URL within a composite parsing scenario.
 *
 * @property url The parsed URL.
 * @property parser The [UrlMatcher] that parsed this URL.
 */
data class CompositeMatcherResult(
    val url: String,
    val parser: UrlMatcher
)