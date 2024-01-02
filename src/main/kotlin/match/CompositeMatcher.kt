package match

/**
 * A composite matcher that aggregates and utilizes a set of service-specific matchers to parse URLs from a given text.
 *
 * @property parsers A set of service-specific matcher to be used for parsing.
 */
class CompositeMatcher(private val parsers: Set<UrlMatcher>) {

    /**
     * Parses the provided text using each service-specific parser and returns a list of URLs.
     *
     * @param string The text to be parsed for URLs.
     * @return A list of URLs parsed from the text using the aggregated matchers.
     */
    fun parse(string: String): List<String> {
        val urlList = mutableListOf<String>()

        for (parser in parsers) {
            urlList += parser.parse(string)
        }

        return urlList
    }
}