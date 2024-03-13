package me.darefox.videosharebot.kord.extensions

import dev.kord.common.entity.PremiumTier
import dev.kord.core.entity.Guild

/**
 * Returns the maximum byte file size allowed for this [Guild], based on its premium tier.
 */
val Guild.maxByteFileSize: Long
    get() = premiumTier.maxByteFileSize()

/**
 * Returns the maximum byte file size allowed for this [Guild], or the default minimum value if the guild is null.
 */
fun Guild?.maxByteFileSizeOrMin(): Long {
    return this?.maxByteFileSize ?: PremiumTier.None.maxByteFileSize()
}

/**
 * Returns the maximum byte file size allowed for the [PremiumTier].
 */
fun PremiumTier.maxByteFileSize(): Long {
    return when (this) {
        PremiumTier.Two -> 50_000_000
        PremiumTier.Three -> 100_000_000
        else -> 25_000_000
    }
}