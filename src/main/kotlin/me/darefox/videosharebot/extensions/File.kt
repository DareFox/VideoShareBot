package me.darefox.videosharebot.extensions

import java.io.File
import java.io.IOException

/**
 * Attempts to immediately delete the file. If unsuccessful, schedules it for deletion on JVM exit.
 * @throws IOException If an I/O error occurs while attempting to delete the file.
 * @return true if file is deleted, false if scheduled to delete file
 */
fun File.deleteGuarantee(): Boolean {
    val result = delete()
    if (!result) {
        deleteOnExit()
    }
    return result
}