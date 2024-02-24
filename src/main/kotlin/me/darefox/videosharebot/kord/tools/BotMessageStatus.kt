package me.darefox.videosharebot.kord.tools

import dev.kord.core.behavior.edit
import kotlinx.coroutines.*
import me.darefox.videosharebot.extensions.createChildScope
import me.darefox.videosharebot.extensions.createLogger
import me.darefox.videosharebot.extensions.onCancel
import me.darefox.videosharebot.kord.extensions.BotMessage
import me.darefox.videosharebot.kord.extensions.isMe
import me.darefox.videosharebot.tools.ArgumentsMode
import me.darefox.videosharebot.tools.DelayMode
import me.darefox.videosharebot.tools.stringtransformers.StringTransformer
import me.darefox.videosharebot.tools.throttleFuncArg
import mu.KotlinLogging
import kotlin.time.Duration.Companion.seconds

class BotMessageStatus(
    private val message: BotMessage,
    private val scope: CoroutineScope,
    override var defaultTransformer: StringTransformer,
) : IBotMessageStatus {
    private val log = createLogger()
    private val messageEditScope = scope.createChildScope(false, Dispatchers.IO)
    private val delayDuration = 2.seconds

    private var _lastEdit: String? = message.ref.content
    override val lastEdit: String?
        get() = _lastEdit

    private var _queued: MessageQueueStatus = MessageQueueStatus.Empty
    override val queued: MessageQueueStatus
        get() = _queued

    override val isActive: Boolean
        get() = messageEditScope.isActive


    private suspend fun editMessage(newContent: String?) {
        _lastEdit = newContent
        message.ref.edit {
            content = newContent
        }
    }

    private val editMessageThrottled = messageEditScope.throttleFuncArg<String?>(
        delayDuration = delayDuration,
        delayMode = DelayMode.DELAY_MINUS_PROCESS_TIME,
        argumentsMode = ArgumentsMode.ONLY_UNIQUE_ARGUMENTS
    ) { newContent ->
        editMessage(newContent)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val onCancel = scope.onCancel(CoroutineName("BotMessageStatus-OnCancel")) {
        log.debug { "BotMessageStatus was cancelled because ${it.message} (throwable: ${it.cause})" }

        if (!isActive) return@onCancel
        messageEditScope.cancel(it)

        val value = _queued
        if (value is MessageQueueStatus.Value) {
            val newContent =  value.content?.let { value.transformer(it) }
            if (newContent == _lastEdit) return@onCancel

            GlobalScope.launch {
                editMessage(newContent)
            }
        }
    }

    override fun changeTo(content: String?, overrideTransformer: StringTransformer?) {
        val (newContent, queue) = transformString(content, overrideTransformer)
        editMessageThrottled(newContent)
        _queued = queue
    }

    override suspend fun cancel(content: String?, overrideTransformer: StringTransformer?) {
        messageEditScope.cancel("cancel was called")
        val (newContent, queue) = transformString(content, overrideTransformer)
        _queued = queue
        editMessage(newContent)
    }

    override fun cancel() {
        messageEditScope.cancel("cancel was called")
    }

    private fun transformString(
        content: String?,
        overrideTransformer: StringTransformer?,
    ): Pair<String?, MessageQueueStatus.Value> {
        val transformer = overrideTransformer ?: defaultTransformer
        val newContent = content?.let { transformer(it) }
        return newContent to MessageQueueStatus.Value(content, transformer)
    }
}