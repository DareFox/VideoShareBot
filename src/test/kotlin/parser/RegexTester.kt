package parser

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

object RegexTester {
    fun shouldMatch(
        regex: Regex,
        input: String,
        expected: String
    ) {
        println("""
            Regex: ${regex.pattern}
            Input: $input
            Expected: $expected
        """.trimIndent() + "\n")

        val value = regex.find(input)?.value
        assertNotNull(value)
        assertEquals(expected, value)
    }

    fun shouldNotMatch(
        regex: Regex,
        input: String,
        illegalMatch: String
    ) {
        println("""
            Regex: ${regex.pattern}
            Input: $input
            Illegal: $illegalMatch
        """.trimIndent() + "\n")

        val value = regex.find(input)?.value
        assertNotEquals(illegalMatch, value)
    }

    fun shouldBeNull(
        regex: Regex,
        input: String
    ) {
        println("""
            Regex: ${regex.pattern}
            Input: $input
            Expected: null
        """.trimIndent() + "\n")

        assertNull(regex.find(input)?.value)
    }
}