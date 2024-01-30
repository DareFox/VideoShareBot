package me.darefox.videosharebot.kord.extensions

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import kotlinx.coroutines.CoroutineScope
import me.darefox.videosharebot.tools.throttleFuncArg
import kotlin.time.Duration.Companion.milliseconds

class BotMessageStatus(private val message: Message, private val scope: CoroutineScope) {
    var status: String = message.content
        get() = field
        set(value) {
            throttled(value)
            field = value
        }

    private val throttled = scope.throttleFuncArg<String>(100.milliseconds, true) {
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
