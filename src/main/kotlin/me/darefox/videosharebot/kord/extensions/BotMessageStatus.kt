package me.darefox.videosharebot.kord.extensions

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import me.darefox.videosharebot.tools.DebouncedFunction
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class BotMessageStatus(private val message: Message) {
    private val debouncedFunction = DebouncedFunction(500.toDuration(DurationUnit.MILLISECONDS)) { status: String? ->
        message.edit {
            content = status
        }
    }

    init {
        val author = requireNotNull(message.author) { "Author is null" }
        require(author.isMe()) {
            "Message is not made by me, but ${author.username}"
        }
    }

    var status: String?
        get() = debouncedFunction.lastValue
        set(value) { debouncedFunction.run(value) }
}
