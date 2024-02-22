package me.darefox.videosharebot.ffmpeg

open class FFmpegOptionValue(
    val flag: String,
    val code: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FFmpegOptionValue

        if (flag != other.flag) return false
        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flag.hashCode()
        result = 31 * result + code
        return result
    }

    override fun toString(): String {
        return "FFmpegOptionValue(stringValue='$flag', intFlag=$code)"
    }
}