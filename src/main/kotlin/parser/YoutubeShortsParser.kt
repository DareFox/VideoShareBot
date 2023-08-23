package parser

import enhancements.toValueList

object YoutubeShortsParser : ServiceUrlParser("YoutubeShorts") {
    private val regex = """${urlStartRegex}youtube\.com/shorts/\S{11}${urlEndMultilineRegex}""".toRegex(
        setOf(RegexOption.MULTILINE)
    )
    override fun parse(text: String): List<String> {
        return regex.findAll(text).toValueList()
    }
}



