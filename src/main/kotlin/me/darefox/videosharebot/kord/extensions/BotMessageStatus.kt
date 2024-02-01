package me.darefox.videosharebot.kord.extensions

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import kotlinx.coroutines.CoroutineScope
import me.darefox.videosharebot.tools.ArgumentsMode
import me.darefox.videosharebot.tools.DelayMode
import me.darefox.videosharebot.tools.throttleFuncArg
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class BotMessageStatus(private val message: Message, private val scope: CoroutineScope) {
    var status: String = message.content
        get() = field
        set(value) {
            throttled(value)
            field = value
        }

    private val throttled = scope.throttleFuncArg<String>(
        delayDuration = 100.milliseconds,
        delayMode = DelayMode.DELAY_MINUS_PROCESS_TIME,
        argumentsMode = ArgumentsMode.ONLY_UNIQUE_ARGUMENTS
    ) {
        message.edit {
            content = it
        }
    }

    init {
        val author = requireNotNull(message.author) { "Author is null" }
        require(author.isMe()) {
            "Message is not made by me, but ${author.username}"
        }
    }
}
