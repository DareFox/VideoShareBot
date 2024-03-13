package me.darefox.videosharebot.extensions

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ClosedSendChannelException

/**
 * Suspends until the given [value] can be sent to this channel, checking if the channel is still open.
 *
 * @return A [ChannelResult] representing the outcome of the operation. Returns [ChannelResult.success] if the value
 * is successfully sent, or [ChannelResult.closed] if the channel is closed.
 */
@OptIn(InternalCoroutinesApi::class)
suspend fun <T> Channel<T>.sendIfOpen(value: T): ChannelResult<Unit> {
    try {
        this.send(value)
        return ChannelResult.success(Unit)
    } catch (ex: Exception) {
        return when (ex) {
            is ClosedSendChannelException->  ChannelResult.closed(ex)
            else -> throw ex
        }
    }
}