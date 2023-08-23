package parser

/**
 * An abstract base class for parsing URLs related to various online services such as YouTube, TikTok, etc.
 *
 * @property name A descriptive name for the specific online service.
 */
sealed class ServiceUrlParser(val name: String) {

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