package me.darefox.videosharebot.ffmpeg

sealed class FFmpegStatus {
    data object NotStarted : FFmpegStatus()
    data class Continue(val progress: FFmpegProgress) : FFmpegStatus()
    data class End(val progress: FFmpegProgress) : FFmpegStatus()
    data class Error(val errorCode: Int, val errorLines: List<String>, val progress: FFmpegProgress?) : FFmpegStatus()
}