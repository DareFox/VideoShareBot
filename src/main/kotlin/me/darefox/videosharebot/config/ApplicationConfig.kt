package me.darefox.videosharebot.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfig(
    @SerialName("discord")
    val discord: DiscordConfig,
    @SerialName("cobalt")
    val cobalt: CobaltConfig,
    @SerialName("optimization")
    val optimization: OptimizationConfig?
)
