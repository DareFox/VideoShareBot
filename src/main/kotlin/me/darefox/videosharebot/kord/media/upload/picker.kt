package me.darefox.videosharebot.kord.media.upload

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import me.darefox.cobaltik.models.PickerType
import me.darefox.cobaltik.wrapper.PickerResponse
import me.darefox.videosharebot.extensions.asCodeBlock
import me.darefox.videosharebot.extensions.asInlineCode
import me.darefox.videosharebot.extensions.filename
import me.darefox.videosharebot.extensions.tryAsResult
import me.darefox.videosharebot.http.requestFile
import me.darefox.videosharebot.kord.extensions.BotMessageStatus
import me.darefox.videosharebot.kord.extensions.maxByteFileSizeOrMin
import kotlin.math.min

private typealias ChunkData = Pair<ByteArray, String>
private typealias ChunkErrorIndexed = Pair<Int, String>

suspend fun uploadPicker(
    response: PickerResponse,
    userMessage: Message,
    botMessage: Message,
    botMessageStatus: BotMessageStatus
) {
    if (response.type == PickerType.VARIOUS) {
        botMessageStatus.status = asInlineCode("${response.type} is not supported")
        return
    }

    var replyTo: Message? = null
    val maxSizeIncluding = botMessage.getGuildOrNull().maxByteFileSizeOrMin()
    val errorList = mutableListOf<ChunkErrorIndexed>()
    val currentChunk = mutableListOf<ChunkData>()

    suspend fun Message.editAndUploadChunk(chunk: List<ChunkData>): Message {
        return this.edit {
            for ((data, filename) in chunk) {
                addFile(filename, ChannelProvider { ByteReadChannel(data) })
            }
        }
    }

    suspend fun Message.replyAndUploadChunk(chunk: List<ChunkData>): Message {
        return this.reply {
            for ((data, filename) in chunk) {
                addFile(filename, ChannelProvider { ByteReadChannel(data) })
            }
        }
    }

    suspend fun sendChunk(chunk: List<ChunkData>) {
        if (replyTo == null) {
            replyTo = botMessage.editAndUploadChunk(chunk)
        } else {
            replyTo = replyTo!!.replyAndUploadChunk(chunk)
        }
    }

    suspend fun addToChunk(index: Int, newItem: ChunkData) {
        if (newItem.first.size >= maxSizeIncluding) {
            errorList += index to "File is too big!"
            return
        }

        if (currentChunk.sumOf { it.first.size } + newItem.first.size >= maxSizeIncluding) {
            sendChunk(currentChunk)
            currentChunk.clear()
        }

        currentChunk += newItem
    }

    for ((index, image) in response.items.withIndex()) {
        botMessageStatus.status = asInlineCode("Finished: $index / ${response.items.size}")
        lateinit var filename: String
        val result = tryAsResult<ByteArray, IOException> {
            requestFile(image.url) {
                filename = it.filename() ?: throw IOException("Can't calculate filename from http response")
                it.bodyAsChannel().toByteArray()
            }
        }

        when (result) {
            is Failure -> errorList += index to (result.reason.message ?: "IOException")
            is Success -> addToChunk(index, result.value to filename)
        }
    }

    sendChunk(currentChunk)
    if (errorList.isNotEmpty()) {
        val header = "Failed to download ${errorList.size} images out of ${response.items.size}\n\n"
        val listString = StringBuilder()

        listString.append("Reasons:\n")
        for ((index, reason) in errorList) {
            listString.append("Image #${index+1}: $reason\n")
        }
        val maxCharacters = 250
        val reasons = listString.toString()
        var stripped = (reasons).substring(0, min(maxCharacters, reasons.length))

        if (stripped.length < reasons.length) {
           stripped += "..."
        }

        val newStatus = asCodeBlock(header + stripped)
        botMessageStatus.status = newStatus
    } else {
        userMessage.edit { suppressEmbeds = true }
        botMessageStatus.status = ""
    }
}