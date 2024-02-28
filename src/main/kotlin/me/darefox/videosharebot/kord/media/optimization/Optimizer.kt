package me.darefox.videosharebot.kord.media.optimization

import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.flow.StateFlow
import me.darefox.videosharebot.extensions.ResultMonad
import me.darefox.videosharebot.tools.ByteSize
import me.darefox.videosharebot.tools.FileExtension
import java.io.InputStream

interface Optimizer {
    val state: StateFlow<OptimizationStatus>

    suspend fun optimizeInput(
        input: InputStream,
        outputSizeLimit: ByteSize,
        extension: FileExtension
    ): ResultMonad<InputStream, OptimizationError>
}

sealed class OptimizationStatus {
    data class Error(override val message: String, val cause: Exception?): OptimizationStatus(), OptimizationError {
        override val asDiscordMessageText: String = "$message ${if (cause != null)  "caused by $cause" else "" }"
        override val discordEmbedBuilder: (EmbedBuilder.() -> Unit)? = null
    }

    data object NotStarted: OptimizationStatus()
    sealed class ValueStatus(val statusValues: Map<String, String>): OptimizationStatus() {
        class InProgress(statusValues: Map<String, String>): ValueStatus(statusValues)
        class End(statusValues: Map<String, String>): ValueStatus(statusValues)
    }
}
