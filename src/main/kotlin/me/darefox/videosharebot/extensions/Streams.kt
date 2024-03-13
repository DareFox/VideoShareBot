package me.darefox.videosharebot.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

/**
 * Creates a pair of piped streams, consisting of a [PipedOutputStream] as the source and a
 * [PipedOutputStream] connected to the source.
 *
 * @return A [PairedInputOutputStreams] object containing the source [PipedInputStream] and sink
 * [PipedOutputStream].
 */
fun createPipedStreams(): PairedInputOutputStreams {
    val source = PipedInputStream()
    val sink = PipedOutputStream(source)
    return PairedInputOutputStreams(source, sink)
}

/**
 * Data class representing paired input and output streams.
 *
 * @property source The [PipedInputStream] serving as the source.
 * @property sink The [PipedOutputStream] connected to the source.
 */
data class PairedInputOutputStreams(
    val source: PipedInputStream,
    val sink: PipedOutputStream,
)

/**
 * Writes the content of this [InputStream] to the specified [OutputStream] in a blocking manner.
 *
 * @param outputStream The destination [OutputStream] to write the content to.
 * @param bufferSize The size of the buffer used during the copying process. Default is 1024 bytes.
 */
fun InputStream.writeToBlocking(outputStream: OutputStream, bufferSize: Int = 1024) {
    runBlocking {
        writeTo(outputStream, bufferSize)
    }
}

/**
 * Suspended function that writes the content of this [InputStream] to the specified [OutputStream] and closes it.
 *
 * @param outputStream The destination [OutputStream] to write the content to.
 * @param bufferSize The size of the buffer used during the copying process. Default is 1024 bytes.
 */
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
    outputStream.close()
}