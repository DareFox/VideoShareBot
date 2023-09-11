package parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ServiceUrlParserTest: ServiceUrlParser("test") {
    val validUrls = listOf<String>(
        "http://api.example.com",
        "http://blog.example.com",
        "http://blog.example.com/products",
        "http://blog.example.com/search?q=term&category=books",
        "http://example.com",
        "http://example.com/",
        "http://example.com/#section1",
        "http://example.com/?id=123",
        "http://example.com/blog/posts",
        "http://example.com/products",
        "http://example.com/search?q=query",
        "http://www.example.com",
        "https://api.example.com",
        "https://api.example.com/blog/post?id=101&ref=link",
        "https://api.example.com/blog/posts",
        "https://blog.example.com",
        "https://example.com",
        "https://example.com/page#section2",
        "https://example.com/product?id=456&category=electronics",
        "https://www.example.com",
        "https://www.example.com/",
        "https://www.example.com/products?id=789",
    )
    val invalidUrls = listOf(
        "http://example",
        "http://",
        "http://example.com:80s",
        "http://my\$subdomain.example.com"
    )

    @Test
    fun `Test Valid Url`() {
        validUrls.forEach {
            RegexTester.shouldMatch(anyUrlRegex, it, it)
        }
    }

    @Test
    fun `Test Invalid Url`() {
        invalidUrls.forEach {
            RegexTester.shouldBeNull(anyUrlRegex, it)
        }
    }

    override fun parse(text: String): List<String> {
        TODO("Not implemented for test")
    }
}