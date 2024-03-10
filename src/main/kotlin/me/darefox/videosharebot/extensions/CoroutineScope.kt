package me.darefox.videosharebot.extensions

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext


fun CoroutineScope.createChildScope(useSupervisor: Boolean, context: CoroutineContext? = null): CoroutineScope {
    val job = when {
        useSupervisor -> SupervisorJob(parent = this.coroutineContext[Job])
        else -> Job(parent = this.coroutineContext[Job])
    }
    val newContext = this.coroutineContext + job + (context ?: EmptyCoroutineContext)
    return CoroutineScope(newContext)
}
}
/**
 * Add job that awaits cancellation and calls callback on cancellation
 *
 * **WARNING: Should be used carefully, because it can create infinite awaiting**
 *
 * **Example of wrong usage: wrapping scope in [coroutineScope] or [withContext].**
 * These functions won't return result until every children finishes and if [onCancel] callback exists, program will await infinitely
 *
 * @param scope The parent coroutine scope to wrap.
 */
@DelicateCoroutinesApi
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