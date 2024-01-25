package me.darefox.videosharebot.extensions

fun asInlineCode(code: String): String {
    return "`$code`"
}

fun asCodeBlock(code: String, lang: String? = null): String {
    val langString = lang ?: ""
    return "```$langString\n${code.trim()}\n```"
}
