package parser

/**
 * An abstract base class for parsing URLs related to various online services such as YouTube, TikTok, etc.
 *
 * @property name A descriptive name for the specific online service.
 */
sealed class ServiceUrlParser(val name: String) {
    /**
     * Regular expression pattern representing the start of a URL, including optional "https://" or "http://" and "www.".
     *
     * ### `(https://|http://|)(www\.|)`
     */
    protected val urlStartRegex = """(https://|http://|)(www\.|)"""

    /**
     * Regular expression pattern representing the end of a URL where it should not be followed by letters, numbers, or slash.
     * Url params are permitted and will be matched
     *
     * ### `(?![a-zA-Z1-9\/])\S*`
     */
    protected val urlEndMultilineRegex = """(?![a-zA-Z1-9\/])\S*"""


    /**
     * Combines the [URL start pattern][urlStartRegex], a custom regex, and the [URL end pattern][urlEndMultilineRegex] to create a complete regex pattern.
     * @param options A set of regex options to be applied to the resulting regex pattern. Defaults to [RegexOption.MULTILINE].
     * @return The combined regex pattern as a compiled regular expression.
     */
    protected fun String.wrapRegexPattern(
        options: Set<RegexOption> = setOf(RegexOption.MULTILINE)
    ) = "$urlStartRegex$this$urlEndMultilineRegex".toRegex(options)

    /**
     * Parses the provided text and returns a list of URLs relevant to this specific online service.
     *
     * @param text The text to be parsed for service-specific URLs.
     * @return A list of URLs for this service, or an empty list if none are found.
     */
    abstract fun parse(text: String): List<String>

    /**
     * Generates a string representation of the online service parser, including its service name and hash code.
     *
     * @return A formatted string containing the parser's service name and hash code.
     */
    override fun toString(): String {
        return "<$name:${hashCode()}>"
    }
}