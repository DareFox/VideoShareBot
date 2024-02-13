package me.darefox.videosharebot.kord.extensions

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.embed
import io.ktor.util.logging.*
import me.darefox.videosharebot.tools.stringtransformers.MarkdownCodeBlock
import me.darefox.videosharebot.tools.stringtransformers.MarkdownCodeInline
import mu.KotlinLogging

suspend inline fun BotMessage.changeToExceptionError(e: Exception): Message {
    return this.ref.edit {
        content = null
        embed {
            title = "ERROR"
            field("Class", true) { MarkdownCodeInline(e.javaClass.name) }
            field("Message", true) { MarkdownCodeInline(e.message.toString()) }
            field("Callstack", false) { MarkdownCodeBlock("kotlin").invoke(e.stackTraceToString())}
            KotlinLogging.logger{  }.error(e)
        }
    }
}