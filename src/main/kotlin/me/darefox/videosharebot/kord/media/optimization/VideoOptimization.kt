package me.darefox.videosharebot.kord.media.optimization

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.darefox.videosharebot.extensions.*
import me.darefox.videosharebot.ffmpeg.*
import me.darefox.videosharebot.ffmpeg.encoders.Libopus
import me.darefox.videosharebot.ffmpeg.encoders.NvencH264
import me.darefox.videosharebot.ffmpeg.encoders.libopus
import me.darefox.videosharebot.ffmpeg.encoders.nvencH264
import me.darefox.videosharebot.tools.*
import me.darefox.videosharebot.kord.media.optimization.FileTooBigAfterOptimizationFault
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class VideoOptimization: Optimizer {
    private val log = createLogger()
    private val _state = MutableStateFlow<OptimizationStatus>(OptimizationStatus.NotStarted)
    override val state: StateFlow<OptimizationStatus> = _state

    override suspend fun optimizeInput(
        input: InputStream,
        outputSizeLimit: ByteSize,
        extension: FileExtension
    ): ResultMonad<InputStream, OptimizationFault> = withContext(Dispatchers.IO) {
        log.info { "Creating temp input file" }
        val tempInput = File.createTempFile("videoFFmpegInput", extension.extension)
        log.info { "Writing to temp input file" }
        val stream = tempInput.outputStream()
        input.writeTo(stream)
        log.info { "Creating temp output file" }
        val tempOutput = File.createTempFile("videoFFmpegOutput", extension.extension)

        var listenerFailure: Failure<OptimizationFault>? = null

        val ffmpeg = buildFFmpeg(tempInput, tempOutput)
        val ffmpegJob = async(CoroutineName("VideoOptimization-FFMPEG")) {
            log.info { "Starting ffmpeg" }
            ffmpeg.process()
        }

        val listener = launch(CoroutineName("VideoOptimization-StatusCollector")) {
            ffmpeg.status.collect { progress ->
                if (isBiggerThanLimit(progress, outputSizeLimit)) {
                    listenerFailure = Failure(FileTooBigAfterOptimizationFault())
                    ffmpegJob.cancel()
                }
                _state.value = progress.convertToOptimizationStatus()
            }
        }

        var isEnd = false
        val cancellationJob = onCancel {
            log.info { "Cancelling listener" }
            listener.cancel()
            log.info { "Deleting temp input file ${tempInput.name} result: " + tempInput.delete() }
            if (!isEnd) {
                log.info { "Optimization was cancelled before ffmpeg finished, deleting temp output" }
                tempOutput.delete()
            } else {
                log.info { "Optimization was cancelled after ffmpeg, returning" }
            }
        }


        try {
            when(val result = ffmpegJob.await()) {
                is Failure -> Failure(result.reason.convertToOptimizationError().value);
                is Success -> Success(Files.newInputStream(tempOutput.toPath(), StandardOpenOption.DELETE_ON_CLOSE))
            }.also {
                isEnd = true
                cancellationJob.cancel()
            }
        } catch (ex: CancellationException) {
            return@withContext listenerFailure ?: throw ex
        }finally {
            println("test")
        }
    }

    private fun isBiggerThanLimit(progress: FFmpegStatus, outputSizeLimit: ByteSize): Boolean {
        return when (progress) {
            is FFmpegStatus.Continue -> progress.progress.totalSize.bytes > outputSizeLimit.bytes
            is FFmpegStatus.End -> progress.progress.totalSize.bytes > outputSizeLimit.bytes
            else -> false
        }
    }
    private fun FFmpegError.convertToOptimizationError() = OptimizationStatus.Fault(
        FFmpegOptimizationFault(
            message = "Exit with code ${exitCode}.${if (errorLines.isNotEmpty()) "\nLog:${errorLines.joinToString("\n")}" else "" }",
            causedBy = null
        )
    )

    private fun FFmpegStatus.convertToOptimizationStatus(): OptimizationStatus {
        return when (this) {
            is FFmpegStatus.Continue -> OptimizationStatus.ValueStatus.InProgress(
                mapOf(
                    "totalBytes" to progress.totalSize.bytes.toByteSize().toString(ByteUnit.Kilobyte),
                    "speed" to progress.speed.toString(),
                    "frame" to progress.frame.toString(),
                    "fps" to progress.fps.toString()
                )
            )
            is FFmpegStatus.End -> OptimizationStatus.ValueStatus.End(
                mapOf(
                    "totalBytes" to progress.totalSize.bytes.toByteSize().toString(ByteUnit.Kilobyte),
                    "speed" to progress.speed.toString(),
                    "frame" to progress.frame.toString(),
                    "fps" to progress.fps.toString()
                )
            )
            is FFmpegStatus.Error -> FFmpegError(errorLines, errorCode).convertToOptimizationError()
            FFmpegStatus.NotStarted -> OptimizationStatus.NotStarted
        }
    }

    private fun buildFFmpeg(tempInput: File, tempOutput: File) = createFFmpeg(
        ffmpegPath = "ffmpeg",
        inputFile = tempInput,
        outputFile = tempOutput
    ) {
        globalOptions {
            hideBanner()
            logLevel(FFmpegLogLevel.Error)
            overwriteFiles()
        }
        encodeOptions {
            nvencH264 {
                rateControl(NvencH264.RateControl.VBR_HQ)
                preset(NvencH264.Preset.P3)
            }
            bitrate(Stream(StreamType.VIDEO), 1.toMegabyte())
            downscaleByBiggestDimension(900)
            libopus {
                application(Libopus.ApplicationType.Audio)
            }
            bitrate(Stream(StreamType.AUDIO), 128.toKilobyte())
            frameRate(30)
        }
    }
}