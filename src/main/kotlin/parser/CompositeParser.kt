package parser

/**
 * A composite parser that aggregates and utilizes a set of service-specific parsers to parse URLs from a given text.
 *
 * @property parsers A set of service-specific parsers to be used for parsing.
 */
class CompositeParser(private val parsers: Set<ServiceUrlParser>) {

    /**
     * Parses the provided text using each service-specific parser and returns a list of URLs.
     *
     * @param string The text to be parsed for URLs.
     * @return A list of URLs parsed from the text using the aggregated parsers.
     */
    fun parse(string: String): List<String> {
        val urlList = mutableListOf<String>()

        for (parser in parsers) {
            urlList += parser.parse(string)
        }

        return urlList
    }
}