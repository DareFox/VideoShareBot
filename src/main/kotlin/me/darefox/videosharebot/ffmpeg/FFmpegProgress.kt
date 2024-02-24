package me.darefox.videosharebot.ffmpeg

import me.darefox.videosharebot.tools.ByteSize

data class FFmpegProgress(
    val frame: Long,
    val fps: Double,
    val speed: Double,
    val totalSize: ByteSize,
)