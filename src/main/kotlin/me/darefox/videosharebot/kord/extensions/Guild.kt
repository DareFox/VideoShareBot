<<<<<<<< Updated upstream:src/main/kotlin/me/darefox/videosharebot/extensions/Guild.kt
package me.darefox.videosharebot.extensions
========
package me.darefox.videosharebot.kord.extensions
>>>>>>>> Stashed changes:src/main/kotlin/me/darefox/videosharebot/kord/extensions/Guild.kt

import dev.kord.common.entity.PremiumTier
import dev.kord.core.entity.Guild

val Guild.maxByteFileSize: Long
    get() = when (this.premiumTier) {
        PremiumTier.Two -> 50_000_000
        PremiumTier.Three -> 100_000_000
        else -> 25_000_000
    }