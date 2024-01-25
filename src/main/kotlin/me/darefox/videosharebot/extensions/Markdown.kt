package me.darefox.videosharebot.extensions

fun String.toSingleCodeLineMarkdown(): String {
    return "`$this`"
}

fun String.toCodeMarkdown(lang: String? = null): String {
    val langString = lang ?: ""
    return "```$langString\n$this\n```"
}