package me.darefox.videosharebot.ffmpeg

import me.darefox.videosharebot.tools.ByteSize

fun FFmpegBuilderStep.GlobalOptions.hideBanner() = +"-hide_banner"

sealed class FFmpegLogLevel(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
    data object Quiet : FFmpegLogLevel("quiet", -8)
    data object Panic : FFmpegLogLevel("panic", 0)
    data object Fatal : FFmpegLogLevel("fatal", 8)
    data object Error : FFmpegLogLevel("error", 16)
    data object Warning : FFmpegLogLevel("warning", 24)
    data object Info : FFmpegLogLevel("info", 32)
    data object Verbose : FFmpegLogLevel("verbose", 40)
    data object Debug : FFmpegLogLevel("debug", 48)
    data object Trace : FFmpegLogLevel("trace", 56)
}

fun FFmpegBuilderStep.GlobalOptions.logLevel(vararg level: FFmpegLogLevel) {
    addSplitOption("-loglevel", level.joinToString("+") {
        it.flag.lowercase()
    })
}

fun FFmpegBuilderStep.GlobalOptions.overwriteFiles() = addSplitOption("-y")
fun FFmpegBuilderStep.GlobalOptions.doNotOverwriteFiles() = addSplitOption("-n")

fun FFmpegBuilderStep.DecodeAndEncodeOptions.frameRate(fps: Int) = addSplitOption("-r", "$fps")

fun FFmpegBuilderStep.EncodeOptions.minrate(bitrate: ByteSize) = addSplitOption("-minrate", "${bitrate.bytes}")
fun FFmpegBuilderStep.EncodeOptions.maxrate(bitrate: ByteSize) = addSplitOption("-maxrate", "${bitrate.bytes}")
fun FFmpegBuilderStep.EncodeOptions.bufsize(bitrate: ByteSize) = addSplitOption("-bufsize", "${bitrate.bytes}")
fun FFmpegBuilderStep.EncodeOptions.bitrate(stream: StreamSpecifier, bitrate: ByteSize) =
    addSplitOption("-b:$stream", "${bitrate.bytes}")

fun FFmpegBuilderStep.EncodeOptions.codec(stream: StreamSpecifier, codec: String) = addSplitOption("-c:$stream", codec)

/**
 * Downscale biggest dimension out of two with saving aspect ratio
 */
fun FFmpegBuilderStep.EncodeOptions.downscaleByBiggestDimension(downscaleTo: Int) {
    addSplitOption("-vf", "scale=w=$downscaleTo:h=$downscaleTo:force_original_aspect_ratio=decrease")
}