package me.darefox.videosharebot.match

/**
 * A functional interface defining the behavior of validating path element within a URL.
 *
 * ## What is path element?
 *
 * Example link: `https://google.com/skyfall/when/you/crumble`
 *
 * Path element in link is `skyfall` ; `when` ; `you` ; `crumble`
 */
fun interface UrlSegmentMatcher {

    /**
     * Examines the provided text and determines whether it aligns with the specified validation criteria.
     *
     * @param text The text of the URL segment to be validated.
     * @return `true` if the text matches the criteria, `false` otherwise.
     */
    fun validate(text: String): Boolean
}
class SpecificText(val shouldBe: String, val ignoreCase: Boolean = false):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.equals(shouldBe, false)
}

class TextLength(val length: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length == length
}

class TextRange(val lengthRange: IntRange): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length in lengthRange
}

object Anything: UrlSegmentMatcher {
    override fun validate(text: String): Boolean = true
}

class TextStartsWith(val prefix: String, val ignoreCase: Boolean = false):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.startsWith(prefix, ignoreCase)
}

class NumberLength(val numberOfDigits: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        text.toLongOrNull() ?: return false
        return text.length == numberOfDigits
    }
}

class CombinedMatcher(vararg val matchers: UrlSegmentMatcher):
    UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        matchers.firstOrNull { it.validate(text) } ?: return false
        return true
    }
}