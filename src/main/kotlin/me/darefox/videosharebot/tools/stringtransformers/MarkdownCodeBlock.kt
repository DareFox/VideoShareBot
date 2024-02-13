package me.darefox.videosharebot.tools.stringtransformers

/**
 * A simple transformer that converts a string into a Markdown code block.
 *
 * @param language The optional programming language of the code (used for syntax highlighting).
 */
class MarkdownCodeBlock(val language: String? = null): StringTransformer {
    override fun invoke(code: String): String {
        val langString = language ?: ""
        return "```$langString\n${code.trim()}\n```"
    }
}