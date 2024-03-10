package me.darefox.videosharebot.optimization

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
    ): ResultMonad<InputStream, OptimizationFault>
}

sealed interface OptimizationStatus {
    data class Fault(val value: OptimizationFault): OptimizationStatus
    data object NotStarted: OptimizationStatus
    sealed class ValueStatus(val statusValues: Map<String, String>): OptimizationStatus {
        class InProgress(statusValues: Map<String, String>): ValueStatus(statusValues)
        class End(statusValues: Map<String, String>): ValueStatus(statusValues)
    }
}
