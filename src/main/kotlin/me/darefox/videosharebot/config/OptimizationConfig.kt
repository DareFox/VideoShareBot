package me.darefox.videosharebot.config

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
enum class OptimizationCodec {
    @SerialName("nvenc")
    Nvenc,
    @SerialName("libx264")
    Libx264,
    @SerialName("quicksync")
    Quicksync,
    @SerialName("h264_amf")
    H264_Amf,
}

@Serializable
data class OptimizationConfig(
    override val isEnabled: Boolean,
    val codec: OptimizationCodec,
    val maxConcurrentSessions: Int = 1,
    val maxQueue: Int = 10,
    val optimizationTimeout: Duration = 2.minutes,
    val serverFilter: FilterRule<Snowflake> = FilterRule(FilterMode.ALL, setOf()),
): Toggleable