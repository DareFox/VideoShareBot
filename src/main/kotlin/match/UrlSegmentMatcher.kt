package match

interface UrlSegmentMatcher {
    fun validate(text: String): Boolean
}

class SpecificText(val shouldBe: String, val ignoreCase: Boolean = false): UrlSegmentMatcher {
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

class TextStartsWith(val prefix: String, val ignoreCase: Boolean): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.startsWith(prefix, ignoreCase)
}

class NumberLength(val numberOfDigits: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        text.toLongOrNull() ?: return false
        return text.length == numberOfDigits
    }
}

class CombinedMatcher(vararg val matchers: UrlSegmentMatcher): UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        matchers.firstOrNull { it.validate(text) } ?: return false
        return true
    }
}