package me.darefox.videosharebot.ffmpeg

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.extensions.createLogger
import me.darefox.videosharebot.tools.toByteSize

class FFmpegStatusConverter {
    private val log = createLogger()
    private val buffer = mutableListOf<String>()

    fun addLine(line: String): FFmpegStatus? {
        buffer += line
        val reverseBuffer = buffer.asReversed()

        val lastString = reverseBuffer.first()
        if (lastString.startsWith("progress=") && reverseBuffer.size > 1) {
            val excludeProgressValue = reverseBuffer.subList(1, reverseBuffer.lastIndex + 1)
            var lastIndexExcluding: Int? = null
            for ((index, value) in excludeProgressValue.withIndex()) {
                if (value.startsWith("progress=")) {
                    lastIndexExcluding = index + 1
                    break
                }
            }

            if (lastIndexExcluding != null) {
                val lines = reverseBuffer.subList(0, lastIndexExcluding)
                val result = convertStringsToFFmpegStatus(lines)

                when (result) {
                    is Failure -> {
                        log.error { "Can't parse progress: ${result.reason}.\nList: ${lines.joinToString("\n")}" }
                        return null
                    }

                    is Success -> {
                        return result.value
                    }
                }
            }
        }

        return null
    }

    private fun searchKey(key: String, lines: List<String>): String? {
        val regex = "(?<=$key=).*".toRegex()

        for (line in lines) {
            val value = regex.find(line)?.value
            if (value != null) return value
        }

        return null
    }

    private fun convertStringsToFFmpegStatus(lines: List<String>): ResultMonad<FFmpegStatus, String> {
        val frame = searchKey("frame", lines)?.toLong() ?: return Failure("frame is null")
        val fps = searchKey("fps", lines)?.toDouble() ?: return Failure("fps is null")

        val doubleRegex = "\\d+\\.\\d+".toRegex()
        val speedRaw = searchKey("speed", lines) ?: return Failure("speed is null")
        val speed = doubleRegex.find(speedRaw)
            ?.value
            ?.toDouble() ?: return Failure("Can't convert speed to double")

        val totalSize = searchKey("total_size", lines)?.toLong()?.toByteSize() ?: return Failure("total_size is null")
        val progressType = searchKey("progress", lines) ?: return Failure("progress is null")

        val progress = FFmpegProgress(
            frame = frame,
            fps = fps,
            speed = speed,
            totalSize = totalSize
        )

        return when (progressType.trim()) {
            "end" -> Success(FFmpegStatus.End(progress))
            "continue" -> Success(FFmpegStatus.Continue(progress))
            else -> Failure("Progress type is not end nor continue (value: ${progressType.trim()})")
        }
    }
}