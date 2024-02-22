package me.darefox.videosharebot.ffmpeg

sealed class StreamSpecifier {
    abstract override fun toString(): String
}

object AllStreams : StreamSpecifier() {
    override fun toString(): String = ""
}

data class Index(val index: Int) : StreamSpecifier() {
    override fun toString(): String = "$index"
}

enum class StreamType(val flag: String) {
    VIDEO("v"),
    AUDIO("a"),
    SUBTITLE("s"),
    DATA("d"),
    ATTACHMENTS("t"),
    ONLY_VIDEO("V")
}

data class Stream(val type: StreamType, val additionalStreamSpecifier: StreamSpecifier? = null) : StreamSpecifier() {
    override fun toString(): String {
        val str = if (additionalStreamSpecifier != null) {
            ":$additionalStreamSpecifier"
        } else {
            ""
        }
        return "${type.flag}$str"
    }
}