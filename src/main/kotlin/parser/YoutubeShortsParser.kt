package parser

import enhancements.toValueList

object YoutubeShortsParser : ServiceUrlParser("YoutubeShorts") {
    private val regex = """youtube\.com/shorts/\S{11}""".toRegex(
        setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
    )
    override fun parse(text: String): List<String> {
        return regex.findAll(text).toValueList()
    }
}



