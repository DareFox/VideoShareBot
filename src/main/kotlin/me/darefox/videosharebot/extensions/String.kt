package me.darefox.videosharebot.extensions

import kotlin.math.floor
import kotlin.math.min

/**
 * Censors sensitive information in the [String] by replacing a middle of it with asterisks.
 * Amount of uncensored characters is calculated linearly by (length / 4.0) with limit of maximum 7 uncensored characters
 *
 * @return The censored [String] with sensitive information replaced by asterisks.
 */
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

/**
 * Censors occurrences of a specified substring in the original string.
 *
 * @param contains The substring to be censored within the original string.
 * @return The string with occurrences of the specified substring replaced by asterisks.
 */
fun String.censorContains(contains: String): String {
    return replace(contains, contains.censor())
}