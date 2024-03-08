package me.darefox.videosharebot.extensions

import kotlin.math.floor
import kotlin.math.min

fun String.censor(): String {
    val notCensoredLength = min(floor((length.toDouble() / 4.0)).toInt(), 7)
    val lastPartLength: Int = notCensoredLength / 2
    val firstPartLength: Int = if (notCensoredLength % 2 != 0) {
        lastPartLength + 1
    } else {
        lastPartLength
    }

    val suffix = substring(length - lastPartLength..lastIndex)
    val prefix = substring(0..<firstPartLength)
    val censored = "*".repeat(length - notCensoredLength)
    return buildString {
        append(prefix)
        append(censored)
        append(suffix)
    }
}

fun String.censorContains(contains: String): String {
    return replace(contains, contains.censor())
}