package parser

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

open class ParserTest(
    val parser: ServiceUrlParser,
    validUrls: List<String>,
    invalidUrls: List<String>,
) {
    private val messageGenerator = MessageGenerator(
        validUrls = validUrls, invalidUrls = invalidUrls
    )


    @Test
    fun `Parsing valid URLs should return expected results`() {
        val messages = messageGenerator.generateValidOnly()

        messages.forEach {
            println(it.string)
            assertContains(iterable = parser.parse(it.string), element = it.url)
        }
    }

    @Test
    fun `Parsing invalid URLs should return nothing`() {
        val messages = messageGenerator.generateInvalidOnly()

        messages.forEach {
            println(it.string)
            assertEquals(expected = listOf(), actual = parser.parse(it.string))
        }
    }

    @Test
    fun `Parsing valid and invalid URLs should return only valid URLs`() {
        val messages = messageGenerator.generateValidAndInvalid()

        messages.forEach {
            println(it.string)
            assertEquals(expected = listOf(it.validUrl), actual = parser.parse(it.string))
        }
    }

    @Test
    fun `Parsing text without valid or invalid URLs should return nothing`() {
        val messages = messageGenerator.generateEmpty()

        messages.forEach {
            println(it)
            assertEquals(expected = listOf(), actual = parser.parse(it))
        }
    }
}