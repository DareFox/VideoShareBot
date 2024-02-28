package me.darefox.videosharebot.extensions

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.createChildScope(useSupervisor: Boolean, context: CoroutineContext? = null): CoroutineScope {
    val job = when {
        useSupervisor -> SupervisorJob(parent = this.coroutineContext[Job])
        else -> Job(parent = this.coroutineContext[Job])
    }
    return (context?.let { CoroutineScope(it) } ?: this) + job
}
fun CoroutineScope.onCancel(context: CoroutineContext? = null, func: (CancellationException) -> Unit): Job {
    val coroutineName = coroutineContext[CoroutineName]?.name ?: "coroutine"
    return launch(CoroutineName("$coroutineName-<onCancel>") + (context ?: EmptyCoroutineContext)) {
        try {
            awaitCancellation()
        } catch (ex: CancellationException) {
            func(ex)
            throw ex
        }
    }
}