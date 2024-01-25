package me.darefox.videosharebot.kord.extensions

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.embed
import me.darefox.videosharebot.extensions.asCodeBlock
import me.darefox.videosharebot.extensions.asInlineCode
import io.ktor.util.logging.*
import mu.KotlinLogging

suspend inline fun Message.changeToExceptionError(e: Exception): Message {
    return this.edit {
        content = null
        embed {
            title = "ERROR"
            field("Class", true) { asInlineCode(e.javaClass.name) }
            field("Message", true) { asInlineCode(e.message.toString()) }
            field("Callstack", false) { asCodeBlock(e.stackTraceToString(), "kotlin") }
            KotlinLogging.logger{  }.error(e)
        }
    }
}