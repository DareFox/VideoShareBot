package me.darefox.videosharebot.kord.extensions

import me.darefox.videosharebot.tools.stringtransformers.StringTransformer

/**
 * Defines the interface for managing string content of a bot message.
 */
interface IBotMessageStatus {
    /**
     * The last edited content of the message.
     */
    val lastEdit: String?
    /**
     * The current status of the message queue. If queued content was emitted, queue will still remain same
     */
    val queued: MessageQueueStatus
    /**
     * Indicates whether the status updates are active.
     */
    val isActive: Boolean

    /**
     * The default string transformer to be used for string content updates.
     */
    var defaultTransformer: StringTransformer

    /**
     * Changes the message content to the given value, optionally overriding a transformer.
     *
     * @param content The new content for the message.
     * @param overrideTransformer An optional transformer to apply to the content that will override [default transformer][defaultTransformer].
     */
    fun changeTo(content: String?, overrideTransformer: StringTransformer? = null)

    /**
     * Cancels the status updates and sets the final message content, optionally overriding a transformer.
     *
     * @param content The final content for the message.
     * @param overrideTransformer An optional transformer to apply to the content that will override [default transformer][defaultTransformer].
     */
    suspend fun cancel(content: String?, overrideTransformer: StringTransformer? = null)

    /**
     * Cancels the status updates without setting any final message content.
     */
    fun cancel()
}

/**
 * Represents the possible states of the message queue.
 */
sealed class MessageQueueStatus {
    /**
     * Indicates that the queue is empty.
     */
    data object Empty : MessageQueueStatus()
    /**
     * Represents a queued content update with chosen transformer.
     */
    class Value(val content: String?, val transformer: StringTransformer): MessageQueueStatus()
}