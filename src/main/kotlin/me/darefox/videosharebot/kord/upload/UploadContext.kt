package me.darefox.videosharebot.kord.upload

import dev.kord.core.entity.Message
import me.darefox.cobaltik.wrapper.WrappedCobaltResponse
import me.darefox.videosharebot.kord.extensions.BotMessage
import me.darefox.videosharebot.kord.tools.BotMessageStatus

data class UploadContext<T: WrappedCobaltResponse>(
    val userMessage: Message,
    val botMessage: BotMessage,
    val botMessageStatus: BotMessageStatus,
    val cobaltResponse: T
)