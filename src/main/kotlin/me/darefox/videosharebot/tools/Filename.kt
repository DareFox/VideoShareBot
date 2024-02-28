package me.darefox.videosharebot.tools

data class Filename(
    val prefix: String,
    val extension: FileExtension
) {
    val fullName: String by lazy { "$prefix${extension.extension}" }
}