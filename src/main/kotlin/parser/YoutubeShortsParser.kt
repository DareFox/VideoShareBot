package parser

import enhancements.toValueList

object YoutubeShortsParser : ServiceUrlParser("YoutubeShorts") {
    private val regex = """youtube\.com/shorts/\S{11}""".wrapRegexPattern()
    override fun parse(text: String): List<String> {
        return regex.findAll(text).toValueList()
    }
}



