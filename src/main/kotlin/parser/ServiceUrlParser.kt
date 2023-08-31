package parser

/**
 * An abstract base class for parsing URLs related to various online services such as YouTube, TikTok, etc.
 *
 * @property name A descriptive name for the specific online service.
 */
abstract class ServiceUrlParser(val name: String) {
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
    protected val urlEndNoSlashMultiline = """(?![a-zA-Z1-9\/])\S*"""

    /**
     * Regular expression pattern representing the end of a URL where it should not be followed by letters, numbers.
     * Url params are permitted and will be matched
     *
     * ### `(?![a-zA-Z1-9\/])\S*`
     */
    protected val urlEndWithSlashMultiline = """(?![a-zA-Z1-9\/])\S*"""



    /**
     * Combines the [URL start pattern][urlStartRegex], a custom regex, and the [URL end pattern][urlEndNoSlashMultiline] to create a complete regex pattern.
     * @param options A set of regex options to be applied to the resulting regex pattern. Defaults to [RegexOption.MULTILINE].
     * @return The combined regex pattern as a compiled regular expression.
     */
    protected fun String.wrapRegexPattern(
        canEndOnSlash: Boolean = true,
        options: Set<RegexOption> = setOf(RegexOption.MULTILINE)
    ) = "$urlStartRegex$this$urlEndNoSlashMultiline".toRegex(options)

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