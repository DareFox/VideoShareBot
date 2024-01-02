package match

interface UrlSegmentMatcher {
    fun validate(text: String): Boolean
}

class SpecificText(val shouldBe: String, val ignoreCase: Boolean = false): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.equals(shouldBe, false)
}

class Text(val length: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length == length
}

class TextRange(val lengthRange: IntRange): UrlSegmentMatcher {
    override fun validate(text: String): Boolean = text.length in lengthRange
}

object Anything: UrlSegmentMatcher {
    override fun validate(text: String): Boolean = true
}

class Number(val length: Int): UrlSegmentMatcher {
    override fun validate(text: String): Boolean {
        text.toLongOrNull() ?: return false
        return text.length == length
    }
}