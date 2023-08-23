package parser

import kotlin.test.Test
import kotlin.test.assertEquals

class YoutubeShortsParserTest: ParserTest(
    parser = YoutubeShortsParser,
    validUrls = listOf(
        "youtube.com/shorts/abcdef12345"
    ),
    invalidUrls = listOf(
        "youtube.com/shorts/abc",
        "youtube.com",
        "youtube.com/shorts/"
    )
)