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
import me.darefox.videosharebot.extensions.filename
import me.darefox.videosharebot.extensions.tryAsResult
import me.darefox.videosharebot.http.requestFile
import me.darefox.videosharebot.kord.extensions.BotMessageStatus
import me.darefox.videosharebot.kord.extensions.maxByteFileSize
import me.darefox.videosharebot.tools.ByteSize
import me.darefox.videosharebot.tools.ByteUnit
import me.darefox.videosharebot.tools.toString
import org.apache.commons.io.input.CountingInputStream

suspend fun uploadStream(eventContext: UploadContext<StreamResponse>) = withContext(Dispatchers.IO) {
    val response = eventContext.cobaltResponse
    val userMessage = eventContext.userMessage
    val botMessage = eventContext.botMessage
    val botMessageStatus = eventContext.botMessageStatus

    botMessageStatus.changeTo("Starting downloading media...")

    lateinit var filename: String
    val maxSizeIncluding = botMessage.ref.getGuild().maxByteFileSize
    val buffered = tryAsResult<ByteArray, IOException> {
        requestFile(response.streamUrl) { http ->
            val expectedSize = http.contentLength() ?: 0
            if (expectedSize >= maxSizeIncluding) {
                throw IOException("File is too big!")
            }

            filename = http.filename() ?: throw IllegalArgumentException("Can't calculate filename by http response")
            readUntilMaxSize(http, botMessageStatus, maxSizeIncluding)
        }
    }

    when (buffered) {
        is Failure -> {
            botMessageStatus.changeTo(buffered.reason.message.toString())
        }
        is Success -> {
            if (buffered.value.size >= maxSizeIncluding) {
                botMessageStatus.changeTo("File is too big!")
                return@withContext
            }
            botMessageStatus.cancel()
            botMessage.ref.edit {
                addFile(filename, ChannelProvider(buffered.value.size.toLong()) {
                    ByteReadChannel(buffered.value)
                })
                content = ""
            }
            userMessage.edit {
                suppressEmbeds = true
            }
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
