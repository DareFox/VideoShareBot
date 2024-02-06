package me.darefox.videosharebot.kord.extensions

import dev.kord.common.entity.PremiumTier
import dev.kord.core.entity.Guild

val Guild.maxByteFileSize: Long
    get() = premiumTier.maxByteFileSize()

fun Guild?.maxByteFileSizeOrMin(): Long {
    return this?.maxByteFileSize ?: PremiumTier.None.maxByteFileSize()
}

fun PremiumTier.maxByteFileSize(): Long {
    return when (this) {
        PremiumTier.Two -> 50_000_000
        PremiumTier.Three -> 100_000_000
        else -> 25_000_000
    }
}