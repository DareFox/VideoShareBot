package me.darefox.videosharebot.tools.stringtransformers

object DoNothingWithString: StringTransformer {
    override fun invoke(p1: String): String = p1
}