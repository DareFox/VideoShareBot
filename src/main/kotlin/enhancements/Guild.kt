package enhancements

import dev.kord.common.entity.PremiumTier
import dev.kord.core.entity.Guild

val Guild.maxByteFileSize: Long
    get() = when(this.premiumTier) {
        PremiumTier.Two -> 50_000_000
        PremiumTier.Three -> 100_000_000
        else -> 25_000_000
    }