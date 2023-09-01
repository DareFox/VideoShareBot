package parser

import kotlin.test.*


class ServiceUrlParserTest: ServiceUrlParser("test") {
    override fun parse(text: String): List<String> {
        TODO("Not yet implemented")
    }

    val baseUrlRegex = "example\\.com"
    val wwwUrlRegex = "(www\\.|)$baseUrlRegex"

    val httpRegex = "$urlStartRegex$baseUrlRegex".toRegex()
    val paramsWithNoSlashRegex = "$wwwUrlRegex$paramsWithoutSlash".toRegex(RegexOption.MULTILINE)

    val baseUrl = "example.com"
    val wwwUrl = "www.example.com"
    val path = "/test"
    val pathWithParams = "/test?param=value"
    val pathWithSlash = "/test/"
    val pathWithParamsAndSlash = "/test?param=value/"
    val ignorePath = "ignoreMe"
    val ignorePathSlash = "$ignorePath/"
    val ignorePathParams = "$ignorePath?param=value"
    val ignorePathParamsAndSlash = "$ignorePath?param=value/"

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
            assertEquals(it, httpRegex.find(it)!!.value)
        }

        val wrongUrls = listOf("htt://", "http:/", "http:///", "htts://", "https:/", "https:///").flatMap {
            listOf(it + baseUrl, it + wwwUrl)
        }

        wrongUrls.forEach {
            val regexValue = httpRegex.find(it)?.value
            assertContains(listOf(baseUrl, wwwUrl), regexValue)
        }
    }

    // paramsWithoutSlash

    @Test
    fun `paramsWithoutSlash shouldn't match if slash at the end`() {
        baseUrls.flatMap { listOf(it + pathWithSlash) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    @Test
    fun `paramsWithoutSlash shouldn't match if next path exists`() {
        baseUrls.flatMap { listOf(it + pathWithSlash + ignorePath) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    @Test
    fun `paramsWithoutSlash shouldn't match if next path with slash exists`() {
        baseUrls.flatMap { listOf(it + pathWithSlash + ignorePathSlash) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    @Test
    fun `paramsWithoutSlash should match path without slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithoutSlash".toRegex()) {
            listOf(it + path)
        }
    }

    @Test
    fun `paramsWithoutSlash should match params without slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithoutSlash".toRegex()) {
            listOf(it + pathWithParams)
        }
    }
    @Test
    fun `paramsWithoutSlash shouldn't match params with slash`() {
        baseUrls.flatMap { listOf(it + pathWithParamsAndSlash) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    @Test
    fun `paramsWithoutSlash shouldn't match if next path with params exists`() {
        baseUrls.flatMap { listOf(it + pathWithSlash + ignorePathParams) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    @Test
    fun `paramsWithoutSlash shouldn't match if next path with params and slash exists`() {
        baseUrls.flatMap { listOf(it + pathWithSlash + ignorePathParamsAndSlash) }.forEach {
            println(it)
            assertNull(paramsWithNoSlashRegex.find(it))
        }
    }

    //
    // paramsWithPossibleSlash
    //

    @Test
    fun `paramsWithPossibleSlash should match path with slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithPossibleSlash".toRegex()) {
            listOf(it + pathWithSlash)
        }
    }

    @Test
    fun `paramsWithPossibleSlash should match path without slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithPossibleSlash".toRegex()) {
            listOf(it + path)
        }`
    }

    @Test
    fun `paramsWithPossibleSlash should match params with slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithoutSlash".toRegex()) {
            listOf(it + pathWithParamsAndSlash)
        }
    }

    @Test
    fun `paramsWithPossibleSlash should match params without slash`() {
        shouldMatch("$wwwUrlRegex$path$paramsWithoutSlash".toRegex()) {
            listOf(it + pathWithParams)
        }
    }

    private fun shouldMatch(regex: String, rightUrlBuilder: (String) -> List<String>) = shouldMatch(regex.toRegex(), rightUrlBuilder)
    private fun shouldMatch(regex: Regex, rightUrlBuilder: (String) -> List<String>) {
        baseUrls
            .flatMap(rightUrlBuilder)
            .forEach {
                printMatch(regex, it)
                val value = regex.find(it)?.value
                assertNotNull(value)
                assertEquals(it, value)
            }
    }

    private fun printMatch(regex: Regex, toMatch: String) {
        println("${regex.pattern}\t$toMatch")
    }
}