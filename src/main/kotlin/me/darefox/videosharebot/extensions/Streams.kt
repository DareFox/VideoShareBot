package me.darefox.videosharebot.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

fun createPipedStreams(): PairedInputOutputStreams {
    val source = PipedInputStream()
    val sink = PipedOutputStream(source)
    return PairedInputOutputStreams(source, sink)
}

data class PairedInputOutputStreams(
    val source: PipedInputStream,
    val sink: PipedOutputStream,
)

fun InputStream.writeToBlocking(outputStream: OutputStream, bufferSize: Int = 1024) {
    runBlocking {
        writeTo(outputStream, bufferSize)
    }
}

suspend fun InputStream.writeTo(outputStream: OutputStream, bufferSize: Int = 1024) = withContext(Dispatchers.IO) {
    while (true) {
        val buffer = ByteArray(bufferSize)
        yield()
        val read = read(buffer)
        if (read == -1) break
        if (read == 0) continue
        yield()
        outputStream.write(buffer, 0, read)
    }
}