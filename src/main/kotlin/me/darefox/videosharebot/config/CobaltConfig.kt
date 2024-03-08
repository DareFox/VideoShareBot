package me.darefox.videosharebot.config

import kotlinx.serialization.Serializable
import me.darefox.videosharebot.extensions.URLSerializer
import me.darefox.videosharebot.tools.ByteSize
import me.darefox.videosharebot.tools.toMegabyte
import java.net.URL
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class CobaltConfig(
    val maxDownloadSize: ByteSize = 750.toMegabyte(),
    val servicesFilter: FilterRule<UrlMatcherMapping> = FilterRule(
        FilterMode.BLACKLIST,
        setOf(UrlMatcherMapping.YouTube)
    ),
    val alwaysDownload: List<UrlMatcherMapping> = listOf(UrlMatcherMapping.Instagram),
    val requestTimeout: Duration = 1.minutes,
    val downloadTimeout: Duration = 3.minutes,
    val sendAudioWithSlideshow: Boolean = true,
    val cobaltApiUrl: @Serializable(with = URLSerializer::class) URL,
)