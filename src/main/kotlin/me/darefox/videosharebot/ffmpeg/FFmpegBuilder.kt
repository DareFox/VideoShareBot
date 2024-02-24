package me.darefox.videosharebot.ffmpeg

import java.io.File

@DslMarker
annotation class FFmpegBuilderMarker

@FFmpegBuilderMarker
data class FFmpegBuilder(
    val ffmpegPath: String,
    val inputFile: String,
    val outputFile: String,
) {
    private val globalInputStep = FFmpegBuilderStep.GlobalOptions()
    private val decodeOptionsStep = FFmpegBuilderStep.DecodeOptions()
    private val encodeOptionsStep = FFmpegBuilderStep.EncodeOptions()

    fun globalOptions(builder: FFmpegBuilderStep.GlobalOptions.() -> Unit) {
        globalInputStep.builder()
    }

    fun decodeOptions(builder: FFmpegBuilderStep.DecodeOptions.() -> Unit) {
        decodeOptionsStep.builder()
    }

    fun encodeOptions(builder: FFmpegBuilderStep.EncodeOptions.() -> Unit) {
        encodeOptionsStep.builder()
    }

    fun build(): FFmpeg = FFmpeg(
        ffmpegPath = ffmpegPath,
        arguments = listOf(
            globalInputStep.build(),
            decodeOptionsStep.build(),
            listOf("-i", inputFile),
            encodeOptionsStep.build(),
            listOf(outputFile)
        ).flatten()
    )
}

fun createFFmpeg(
    ffmpegPath: String,
    inputFile: String,
    outputFile: String,
    builderFunc: FFmpegBuilder.() -> Unit
) = FFmpegBuilder(ffmpegPath, inputFile, outputFile).apply(builderFunc).build()

fun createFFmpeg(
    ffmpegPath: String,
    inputFile: File,
    outputFile: File,
    builderFunc: FFmpegBuilder.() -> Unit
) = createFFmpeg(ffmpegPath, inputFile.absolutePath, outputFile.absolutePath, builderFunc)

fun createFFmpeg(
    ffmpegPath: File,
    inputFile: File,
    outputFile: File,
    builderFunc: FFmpegBuilder.() -> Unit
) = createFFmpeg(ffmpegPath.absolutePath, inputFile, outputFile, builderFunc)