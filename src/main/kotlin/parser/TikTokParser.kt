package parser

import enhancements.findAll
import enhancements.toRegex
import enhancements.toValueList

object TikTokParser : ServiceUrlParser("TikTok") {
    private val regex = listOf(
        """tiktok\.com/[^\s\/]+?/video/\S{19}""",
        """vm\.tiktok\.com/.{9}"""
    ).map { it.wrapRegexPattern() }

    override fun parse(text: String): List<String> {
        return regex.findAll(text).toValueList()
    }
}