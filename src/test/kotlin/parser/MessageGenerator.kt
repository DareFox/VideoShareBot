package parser

/**
 * A class for generating text messages with URLs.
 *
 * @property validUrls A list of valid URLs to be used for generating messages.
 * @property invalidUrls A list of invalid URLs to be used for generating messages.
 */
class MessageGenerator(val validUrls: List<String>, val invalidUrls: List<String>) {
    /** Regular expression for replacing placeholder '%url%' in text templates */
    private val replaceRegex = "%url%".toRegex()
    /** List of text templates containing '%url%' placeholders */
    private val textTemplates = listOf<String>(
        "Check this video %url%",
        "this: %url%",
        "its not that good- %url%",
        "%url%",
        "-> %url% - Watch the video now!",
        "= %url% ="
    )

    /**
     * Generates replacement text by combining valid and invalid URLs in different ways.
     *
     * @param validUrl The valid URL to be used in replacement.
     * @param invalidUrl The invalid URL to be used in replacement.
     * @return A list of replacement texts.
     */
    private fun generateReplacementText(validUrl: String, invalidUrl: String): List<String> {
        return listOf(
            "$validUrl and $invalidUrl",
            "$invalidUrl and $validUrl",
            "$validUrl $invalidUrl",
            "$invalidUrl $validUrl",
            "$validUrl $invalidUrl",
            "$invalidUrl, $validUrl",
        )
    }

    /**
     * Generates a list of [UrlText] instances by replacing '%url%' placeholders in text templates.
     *
     * @param urlList The list of URLs to be used for replacement.
     * @return A list of [UrlText] instances.
     */
    private fun generateText(urlList: List<String>): List<UrlText> {
        return urlList.flatMap { url ->
            textTemplates.map { text ->
                UrlText(
                    string = text.replace(replaceRegex, url),
                    url = url
                )
            }
        }
    }

    /**
     * Replaces '%url%' placeholders in text templates with a provided replacement URL.
     *
     * @param replacementUrl The URL to be used for replacement.
     * @return A list of texts with '%url%' placeholders replaced.
     */
    private fun replaceText(replacementUrl: String): List<String> {
        return textTemplates.map {
            it.replace(replaceRegex, replacementUrl)
        }
    }

    /**
     * Generates texts by replacing '%url%' placeholders with valid and invalid URLs.
     *
     * @param valid The valid URL to be used in replacement.
     * @param invalid The invalid URL to be used in replacement.
     * @return A list of [BothUrlText] instances.
     */
    private fun replaceText(valid: String, invalid: String): List<BothUrlText> {
        return generateReplacementText(valid, invalid).flatMap { replacement ->
            replaceText(replacement).map {
                BothUrlText(
                    string = it,
                    validUrl = valid,
                    invalidUrl = invalid
                )
            }
        }
    }

    /**
     * Generates a list of texts by including all possible valid and invalid URLs pairs.
     *
     * @return A list of [BothUrlText] instances.
     */
    fun generateValidAndInvalid(): List<BothUrlText> {
        return validUrls.flatMap { valid ->
            invalidUrls.flatMap { invalid ->
                replaceText(valid, invalid)
            }
        }
    }
    /**
     * Generates a list of [UrlText] instances including valid URLs.
     *
     * @return A list of [UrlText] instances.
     */
    fun generateValidOnly(): List<UrlText> = generateText(validUrls)
    /**
     * Generates a list of [UrlText] instances including invalid URLs.
     *
     * @return A list of [UrlText] instances.
     */
    fun generateInvalidOnly(): List<UrlText> = generateText(invalidUrls)
    /**
     * Generates a list of texts with placeholders replaced by an empty string.
     *
     * @return A list of texts with empty placeholders.
     */
    fun generateEmpty(): List<String> = replaceText("")

    /**
     * Data class representing a text string with associated valid and invalid URLs.
     *
     * @property string The text with placeholders replaced.
     * @property validUrl The valid URL associated with the text.
     * @property invalidUrl The invalid URL associated with the text.
     */
    data class BothUrlText(
        val string: String,
        val validUrl: String,
        val invalidUrl: String
    )

    /**
     * Data class representing a text string with an associated URL.
     *
     * @property string The text with placeholders replaced.
     * @property url The URL that replaced placeholder in text.
     */
    data class UrlText(
        val string: String,
        val url: String
    )
}