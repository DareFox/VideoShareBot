package parser

import kotlin.test.*


class ServiceUrlParserTest: ServiceUrlParser("test") {
    override fun parse(text: String): List<String> {
        TODO("Not yet implemented")
    }

    val baseUrlRegex = "example\\.com"
    val possibleWwwUrlsRegex = "(www\\.|)$baseUrlRegex"

    val startAndBaseUrlRegex = "$urlStartRegex$baseUrlRegex".toRegex()
    val baseUrlEndNoSlashRegex = "$possibleWwwUrlsRegex$urlEndNoSlashMultiline".toRegex(RegexOption.MULTILINE)
    val fullUrlNoSlashRegex = "$urlStartRegex$baseUrlRegex$urlEndNoSlashMultiline".toRegex(RegexOption.MULTILINE)
    val baseUrlEndWithSlashRegex = "$possibleWwwUrlsRegex$urlEndWithSlashMultiline".toRegex(RegexOption.MULTILINE)
    val fullUrlEndWithSlashRegex = "$urlStartRegex$baseUrlRegex$urlEndWithSlashMultiline".toRegex(RegexOption.MULTILINE)

    val baseUrl = "example.com"
    val wwwUrl = "www.example.com"
    val path = "/test"
    val pathWithParams = "/test?param=value"
    val pathWithSlash = "/test/"
    val pathWithParamsAndSlash = "/test?param=value/"
    val secondPathToIgnore = "/ignoreMe"
    val secondPathToIgnoreWithSlash = "/ignoreMe/"

    val baseUrls = listOf(
        baseUrl,
        wwwUrl
    )

    @Test
    fun `Test urlStartRegex`() {
        val rightUrls = listOf("http://", "https://").flatMap {
            listOf(it + baseUrl, it + wwwUrl)
        }

        rightUrls.forEach {
            assertEquals(it, startAndBaseUrlRegex.find(it)!!.value)
        }

        val wrongUrls = listOf("htt://", "http:/", "http:///", "htts://", "https:/", "https:///").flatMap {
            listOf(it + baseUrl, it + wwwUrl)
        }

        wrongUrls.forEach {
            val regexValue = startAndBaseUrlRegex.find(it)?.value
            assertContains(listOf(baseUrl, wwwUrl), regexValue)
        }
    }

    @Test
    fun `urlEndNoSlashMultiline should not match with slash`() {
        baseUrls.flatMap {
            listOf(it + pathWithSlash, it + pathWithParamsAndSlash)
        }.forEach {
            assertNull(baseUrlEndNoSlashRegex.find(it))
        }
    }

    @Test
    fun `urlEndNoSlashMultiline should match without slash`() {
        baseUrls.map {
            it + path
        }.forEach {
            println(it)
            val regex = "$possibleWwwUrlsRegex$path$urlEndNoSlashMultiline".toRegex()
            val value = regex.find(it)?.value
            assertNotNull(value)
            assertEquals(it, value)
        }
    }

    @Test
    fun `urlEndNoSlashMultiline should match params without slash`() {
        baseUrls.map {
            it + pathWithParams
        }.forEach {
            println(it)
            val regex = "$possibleWwwUrlsRegex$path$urlEndNoSlashMultiline".toRegex()
            val value = regex.find(it)?.value
            assertNotNull(value)
            assertEquals(it, value)
        }
    }

    @Test
    fun `urlEndWithSlashMultiline should match params with slash`() {
        baseUrls.map {
            it + pathWithSlash
        }.forEach {
            println(it)
            val regex = "$possibleWwwUrlsRegex$path$urlEndNoSlashMultiline".toRegex()
            val value = regex.find(it)?.value
            assertNotNull(value)
            assertEquals(it, value)
        }
    }

    private fun shouldMatch(regex: String, rightUrlBuilder: (String) -> List<String>) = shouldMatch(regex.toRegex(), rightUrlBuilder)

    private fun shouldMatch(regex: Regex, rightUrlBuilder: (String) -> List<String>) {
        baseUrls
            .flatMap(rightUrlBuilder)
            .forEach {
                println(it)
                val value = regex.find(it)?.value
                assertNotNull(value)
                assertEquals(it, value)
            }
    }
}