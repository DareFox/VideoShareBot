package me.darefox.videosharebot.tools.stringtransformers

/**
 * A simple transformer that converts a string into a Markdown inline code snippet.
 *
 * @see MarkdownCodeBlock for transforming entire code blocks.
 */
object MarkdownCodeInline: StringTransformer {
    override fun invoke(p1: String): String = "`$p1`"
}