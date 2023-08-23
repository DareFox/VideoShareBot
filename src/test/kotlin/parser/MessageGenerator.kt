package parser

class MessageGenerator(val validUrls: List<String>, val invalidUrls: List<String>) {
    private val replaceRegex = "%url%".toRegex()
    private val textTemplates = listOf<String>(
        "Check this video %url%",
        "this: %url%",
        "its not that good- %url%",
        "%url%",
        "-> %url% - Watch the video now!",
        "= %url% ="
    )

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

    private fun replaceText(replacementUrl: String): List<String> {
        return textTemplates.map {
            it.replace(replaceRegex, replacementUrl)
        }
    }

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

    fun generateValidAndInvalid(): List<BothUrlText> {
        return validUrls.flatMap { valid ->
            invalidUrls.flatMap { invalid ->
                replaceText(valid, invalid)
            }
        }
    }
    fun generateValidOnly(): List<UrlText> = generateText(validUrls)
    fun generateInvalidOnly(): List<UrlText> = generateText(invalidUrls)
    fun generateEmpty(): List<String> = replaceText("")

    data class BothUrlText(
        val string: String,
        val validUrl: String,
        val invalidUrl: String
    )

    data class UrlText(
        val string: String,
        val url: String
    )
}