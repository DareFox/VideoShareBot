package me.darefox.videosharebot.tools.stringtransformers

/**
 * A functional interface representing a simple string transformer.
 *
 * @param String Input string.
 * @return [String] - Transformed string.
 */
fun interface StringTransformer: (String) -> String