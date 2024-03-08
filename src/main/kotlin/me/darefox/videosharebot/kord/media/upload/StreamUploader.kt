package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.*
import dev.kord.core.behavior.edit
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import kotlinx.io.Buffer
import kotlinx.io.asInputStream
import kotlinx.io.readByteArray
import me.darefox.cobaltik.wrapper.StreamResponse
import me.darefox.videosharebot.config.GlobalApplicationConfig
import me.darefox.videosharebot.config.isEnabled
import me.darefox.videosharebot.extensions.*
import me.darefox.videosharebot.http.requestFile
import me.darefox.videosharebot.kord.extensions.maxByteFileSize
import me.darefox.videosharebot.kord.media.optimization.OptimizationError
import me.darefox.videosharebot.kord.media.optimization.OptimizationStatus
import me.darefox.videosharebot.kord.media.optimization.Optimizer
import me.darefox.videosharebot.kord.media.optimization.VideoOptimization
import me.darefox.videosharebot.kord.tools.BotMessageStatus
import me.darefox.videosharebot.tools.*
import java.io.InputStream
import java.io.PipedOutputStream

// TODO: Refactor StreamUpload and UploaderFactory to support all configurations options
data object StreamUploader : Uploader<StreamResponse, StreamError>() {
    private val log = this.createLogger()
    override suspend fun upload(context: UploadContext<StreamResponse>) = withContext(Dispatchers.IO) {
        val (userMessage, botMessage, botMessageStatus, response) = context

        botMessageStatus.changeTo("Starting downloading media...")

        lateinit var filename: Filename
        val maxSizeIncluding = botMessage.ref.getGuild().maxByteFileSize.toByteSize()

        val result = requestFile(response.streamUrl) { http ->
            log.info { "Requesting ${response.streamUrl}" }
            filename = http.filename() ?: return@requestFile Failure(CantGetFilename)
            val optimization = when {
                isEnabled(GlobalApplicationConfig.optimization) -> VideoOptimization()
                else -> null
            }
            http.read(botMessageStatus, maxSizeIncluding, filename, optimization)
        }

        val stream = when (result) {
            is Failure -> return@withContext result
            is Success -> result.value
        }

        botMessageStatus.cancel()
        botMessage.ref.edit {
            content = null
            addFile(filename.fullName, ChannelProvider { stream.toByteReadChannel() })
        }
        userMessage.edit {
            suppressEmbeds = true
        }

        Success()
    }


    private suspend fun HttpResponse.read(
        botMessageStatus: BotMessageStatus,
        limit: ByteSize,
        filename: Filename,
        optimizer: Optimizer?
    ): ResultMonad<InputStream, StreamError> = withContext(Dispatchers.IO + CoroutineName("StreamUploader-Read()")) {
        val (source, sink) = createPipedStreams()
        val expectedSize = contentLength()?.toByteSize()
        var optimizationResult: ResultMonad<InputStream, OptimizationError>? = null
        var optimizationJob: Job? = null
        var readJob: Deferred<Result<Buffer, StreamError>>? = null

        val cancellation = onCancel {
            log.logCancel(it)
            optimizationJob?.cancel()
            readJob?.cancel()
            sink.close()
            source.close()
        }

        if (optimizer != null) {
            optimizationJob = launch(
                CoroutineName("StreamUploader-Optimization"),
                CoroutineStart.LAZY
            ) {
                val collector = launch(CoroutineName("StreamUploader-Optimizaiton-StatusCollector")) {
                    optimizer.state.collect {
                        botMessageStatus.changeToOptimizationStatus(it)
                    }
                }
                optimizationResult =
                    optimizer.optimizeInput(source, limit, filename.extension).peekFailure {
                        log.error { "Optimization job is failure, cancelling reading job" }
                        readJob?.cancel()
                    }
                collector.cancel()
            }
        }

        readJob = async(CoroutineName("StreamUploader-Reader")) {
            readWithOptimization(
                limit = limit,
                expectedSize = expectedSize,
                optimizationJob = optimizationJob,
                sink = sink,
                botMessageStatus = botMessageStatus
            )
        }

        val readResult = readJob.await()
        if (optimizationJob?.isActive == true) {
            optimizationJob.join()
        }
        cancellation.cancel()

        val buffer = when (readResult) {
            is Failure -> return@withContext readResult
            is Success -> readResult.value
        }

        return@withContext when (val result = optimizationResult) {
            is Failure -> Failure(StreamOptimizationError(result.reason))
            is Success -> Success(result.value)
            null -> Success(buffer.asInputStream())
        }
    }

    private suspend fun HttpResponse.readWithOptimization(
        limit: ByteSize,
        expectedSize: ByteSize?,
        optimizationJob: Job?,
        sink: PipedOutputStream,
        botMessageStatus: BotMessageStatus
    ): ResultMonad<Buffer, StreamError> {
        val buffer = Buffer()
        val channel = bodyAsChannel()
        val sizeUnit = ByteUnit.Megabyte
        var offset = 0L

        fun limitBehaviour(buffer: Buffer): Failure<StreamError>? {
            when (offset >= limit.bytes || limit.bytes < (expectedSize?.bytes ?: 0)) {
                true -> if (optimizationJob != null) {
                    if (!optimizationJob.isActive) {
                        log.info {
                            val expectedSizeConverted = expectedSize?.toString(sizeUnit)
                            val limitConverted = limit.toString(sizeUnit)
                            "Read bytes is equal or bigger than limit ($limitConverted), starting optimization job. Expected size of original: $expectedSizeConverted"
                        }
                        log.info { "Started successfully: ${optimizationJob.start()}" }
                    }
                    sink.write(buffer.readByteArray())
                    return null
                } else {
                    log.info { "File is too big, returning failure" }
                    return Failure(FileIsTooBig)
                }
                false -> return null
            }
        }

        while (true) {
            val byteSizeOffset = offset.toByteSize()
            val remaining = expectedSize?.toString(sizeUnit) ?: "???"
            val current = byteSizeOffset.toString(sizeUnit)
            botMessageStatus.changeTo("$current / $remaining")

            val byteArray = ByteArray(1.toKilobyte().bytes.toInt())
            val currentRead = channel.readAvailable(byteArray, 0, byteArray.size)

            when {
                currentRead > 0 -> {
                    buffer.write(byteArray.sliceArray(0..<currentRead))
                    offset += currentRead
                    when (val result = limitBehaviour(buffer)) {
                        is Failure<StreamError> -> return result
                    }
                }

                currentRead == -1 -> {
                    sink.close()
                    break
                }
            }
        }

        return Success(buffer)
    }

    private fun BotMessageStatus.changeToOptimizationStatus(status: OptimizationStatus) {
        when (status) {
            is OptimizationStatus.ValueStatus -> {
                val string = status.statusValues.map {
                    "${it.key.uppercase()}: ${it.value}"
                }.joinToString(", ")
                val progress = when (status) {
                    is OptimizationStatus.ValueStatus.End -> "Optimized"
                    is OptimizationStatus.ValueStatus.InProgress -> "Optimizing"
                }
                changeTo("$progress. $string")
            }

            else -> {}
        }
    }
}