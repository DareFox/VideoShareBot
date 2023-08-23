package parser

import kotlin.test.Test
import kotlin.test.assertEquals

class YoutubeShortsParserTest {
    val parser = YoutubeShortsParser

    @Test
    fun `Parsing valid YouTube Shorts URLs should return expected results`() {
        // Arrange
        val text = "Check out this amazing YouTube Shorts: youtube.com/shorts/abcdef12345"

        // Act
        val result = parser.parse(text)

        // Assert
        val expectedUrls = listOf("youtube.com/shorts/abcdef12345")
        assertEquals(expectedUrls, result)
    }

    @Test
    fun `Parsing text without YouTube Shorts URLs should return an empty list`() {
        // Arrange
        val text = "This text does not contain any YouTube Shorts URLs."

        // Act
        val result = parser.parse(text)

        // Assert
        val expectedUrls = emptyList<String>()
        assertEquals(expectedUrls, result)
    }

    @Test
    fun `Parsing mixed text with YouTube Shorts and non-matching URLs should return expected results`() {
        // Arrange
        val text = "Here's a YouTube Shorts: youtube.com/shorts/abcdef12345, and a regular URL: example.com."

        // Act
        val result = parser.parse(text)

        // Assert
        val expectedUrls = listOf("youtube.com/shorts/abcdef12345")
        assertEquals(expectedUrls, result)
    }

    @Test
    fun `Ignore wrong youtube shorts url`() {
        // Arrange
        val text = "Here's a YouTube Shorts: youtube.com/shorts/nuhuh, and a regular URL: example.com."

        // Act
        val result = parser.parse(text)

        // Assert
        val expectedUrls = listOf<String>()
        assertEquals(expectedUrls, result)
    }

    @Test
    fun `Ignore params after youtube shorts url`() {
        // Arrange
        val text = "Here's a YouTube Shorts: youtube.com/shorts/abcdef12345?feature=share, and a regular URL: example.com."

        // Act
        val result = parser.parse(text)

        // Assert
        val expectedUrls = listOf("youtube.com/shorts/abcdef12345")
        assertEquals(expectedUrls, result)
    }
}