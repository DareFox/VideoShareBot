package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.kord.core.behavior.edit
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import me.darefox.cobaltik.wrapper.StreamResponse
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.extensions.Success
import me.darefox.videosharebot.extensions.filename
import me.darefox.videosharebot.extensions.tryAsResult
import me.darefox.videosharebot.http.requestFile
import me.darefox.videosharebot.kord.tools.BotMessageStatus
import me.darefox.videosharebot.kord.extensions.maxByteFileSize
import me.darefox.videosharebot.tools.ByteSize
import me.darefox.videosharebot.tools.ByteUnit
import me.darefox.videosharebot.tools.toString
import org.apache.commons.io.input.CountingInputStream

data object StreamUploader : Uploader<StreamResponse, StreamError>() {
    override suspend fun upload(context: UploadContext<StreamResponse>) = withContext(Dispatchers.IO) {
        val response = context.cobaltResponse
        val userMessage = context.userMessage
        val botMessage = context.botMessage
        val botMessageStatus = context.botMessageStatus

        botMessageStatus.changeTo("Starting downloading media...")

        lateinit var filename: String
        val maxSizeIncluding = botMessage.ref.getGuild().maxByteFileSize
        val buffered = tryAsResult<ResultMonad<ByteArray, StreamError>, IOException> {
            requestFile(response.streamUrl) { http ->
                val expectedSize = http.contentLength() ?: 0
                if (expectedSize >= maxSizeIncluding) {
                    return@requestFile Failure(FileIsTooBig)
                }

                filename = http.filename() ?: throw IllegalArgumentException("Can't calculate filename by http response")
                Success(readUntilMaxSize(http, botMessageStatus, maxSizeIncluding))
            }
        }

        val result = when (buffered) {
            is Failure -> return@withContext Failure(IOError(buffered.reason))
            is Success -> buffered.value
        }

        when (result) {
            is Failure -> return@withContext Failure(result.reason)
            is Success -> {
                if (result.value.size >= maxSizeIncluding) {
                    return@withContext Failure(FileIsTooBig)
                }
                botMessageStatus.cancel()
                botMessage.ref.edit {
                    addFile(filename, ChannelProvider(result.value.size.toLong()) {
                        ByteReadChannel(result.value)
                    })
                    content = ""
                }
                userMessage.edit {
                    suppressEmbeds = true
                }
                return@withContext Success()
            }
        }
    }

    private suspend fun readUntilMaxSize(
        http: HttpResponse,
        botMessageStatus: BotMessageStatus,
        maxSizeIncluding: Long
    ): ByteArray = withContext(Dispatchers.IO) {
        val size = http.contentLength()?.let { ByteSize(it) }
        val count = CountingInputStream(http.bodyAsChannel().toInputStream())

        val counter = launch {
            while (true) {
                val read = ByteSize(count.byteCount)
                val progress = "${read.toString(ByteUnit.Megabyte)} / ${size?.toString(ByteUnit.Megabyte) ?: "???"}"
                botMessageStatus.changeTo(progress)
                delay(50L)
            }
        }

        val buffered = count.readNBytes(maxSizeIncluding.toInt())
        counter.cancelAndJoin()
        buffered
    }

}