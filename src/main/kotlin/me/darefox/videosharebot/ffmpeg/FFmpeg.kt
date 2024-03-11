package me.darefox.videosharebot.ffmpeg

import dev.forkhandles.result4k.Failure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import me.darefox.videosharebot.extensions.*
import raceWithCancellation
import kotlin.streams.asSequence
import kotlin.time.Duration.Companion.seconds

class FFmpeg(
    val ffmpegPath: String,
    val arguments: List<String>,
) {
    private val log = createLogger()
    private val fullCommand = getFullCommand("-stats_period 0.5 -progress pipe:1".split(" "))
    private val processBuilder = ProcessBuilder(fullCommand)
    private var process: Process? = null

    private val _status = MutableStateFlow<FFmpegStatus>(FFmpegStatus.NotStarted)
    val status = _status as StateFlow<FFmpegStatus>

    suspend fun process(): ResultMonad<Unit, FFmpegError> = coroutineScope {
        val processScope = CoroutineScope(Dispatchers.IO)
        val cancellation = onCancel {
            log.logCancel(it)
            killFFmpeg()
            processScope.cancel()
        }

        log.info { "Starting ffmpeg with command:\n$fullCommand" }
        val processRef = withContext(Dispatchers.IO) {
            processBuilder.start()
        }
        process = processRef
        val errorList = mutableListOf<String>()

        processScope.apply {
            launch {
                collectErrorsToList(processRef, errorList)
            }
            launch(CoroutineName("StatusConverter")) {
                convertOutputToStatus(processRef)
            }
        }

        val code = withContext(Dispatchers.IO) {
            processRef.waitFor()
        }
        cancellation.cancel()

        when {
            code != 0 -> {
                setStatusToError(code, errorList)
                Failure(FFmpegError(errorList, code))
            }
            else -> Success()
        }
    }

    private fun setStatusToError(code: Int, errorLines: List<String>) {
        _status.updateAndGet {
            when (it) {
                is FFmpegStatus.Continue -> FFmpegStatus.Error(code, errorLines, it.progress)
                is FFmpegStatus.End -> throw IllegalStateException("You shouldn't get here")
                is FFmpegStatus.Error -> it
                FFmpegStatus.NotStarted -> FFmpegStatus.Error(code, errorLines, null)
            }
        }
    }

    private suspend fun collectErrorsToList(processRef: Process, errorList: MutableList<String>) {
        processRef.errorStream.bufferedReader().lines().asSequence().forEach {
            log.error { "STDERR: $it" }
            errorList += it
            yield()
        }
    }

    private suspend fun convertOutputToStatus(processRef: Process) {
        val converter = FFmpegStatusConverter()
        processRef.inputStream.bufferedReader().lines().asSequence().forEach {
            log.debug { "STDIN: $it" }
            val progress = converter.addLine(it)
            if (progress != null) _status.value = progress
            yield()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun killFFmpeg() {
        GlobalScope.launch {
            raceWithCancellation {
                addRacer(CoroutineName("Attempt to kill gracefully")) {
                    if (process?.isAlive == false) return@addRacer
                    log.debug { "Trying to kill gracefully" }
                    process?.destroy()
                    log.debug { "Killed gracefully" }
                }
                addRacer(CoroutineName("Timeout forced kill")) {
                    delay(20.seconds)
                    process?.destroyForcibly()
                    log.debug { "Killed by force" }
                }
            }
        }
    }

    private fun getFullCommand(globalOptions: List<String>) = listOf(ffmpegPath) + globalOptions + arguments
}

data class FFmpegError(
    val errorLines: List<String>,
    val exitCode: Int
)

