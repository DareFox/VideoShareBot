package me.darefox.videosharebot.config

import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.darefox.videosharebot.tools.ColorRGB

@Serializable
sealed class PresenceAction {
    @Serializable
    @SerialName("none")
    data object None: PresenceAction()
    @Serializable
    @SerialName("competing")
    data class Competing(val name: String): PresenceAction()
    @Serializable
    @SerialName("playing")
    data class Playing(val name: String): PresenceAction()
    @Serializable
    @SerialName("listening")
    data class Listening(val name: String): PresenceAction()
    @Serializable
    @SerialName("watching")
    data class Watching(val name: String): PresenceAction()
}

@Serializable
enum class PresenceStatusEnum(val status: PresenceStatus) {
    @SerialName("dnd")
    DoNotDisturb(PresenceStatus.DoNotDisturb),
    @SerialName("idle")
    Idle(PresenceStatus.Idle),
    @SerialName("invisible")
    Invisible(PresenceStatus.Invisible),
    @SerialName("offline")
    Offline(PresenceStatus.Offline),
    @SerialName("online")
    Online(PresenceStatus.Online),
}

@Serializable
data class DiscordConfig (
    val token: String,
    val presenceStatus: PresenceStatusEnum = PresenceStatusEnum.Online,
    val presenceAction: PresenceAction = PresenceAction.None,
    val serverFilter: FilterRule<Snowflake> = FilterRule(FilterMode.ALL, setOf()),
    val embedColor: ColorRGB = ColorRGB.fromHex("#ffffff"),
)
