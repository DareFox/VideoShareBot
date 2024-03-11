package me.darefox.videosharebot.extensions

import kotlinx.coroutines.*
import raceWithCancellation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration



/**
 * Creates a child coroutine scope, optionally using a supervisor job for error isolation.
 *
 * @param useSupervisor If true, uses a supervisor job for the child scope.
 * @param context Additional coroutine context elements to be added to the child scope.
 * @return The created child coroutine scope.
 */
fun CoroutineScope.createChildScope(useSupervisor: Boolean, context: CoroutineContext? = null): CoroutineScope {
    val job = when {
        useSupervisor -> SupervisorJob(parent = this.coroutineContext[Job])
        else -> Job(parent = this.coroutineContext[Job])
    }
    val newContext = this.coroutineContext + job + (context ?: EmptyCoroutineContext)
    return CoroutineScope(newContext)
}

/**
 * Creates a child coroutine scope with the given context, optionally using a supervisor job.
 *
 * @param useSupervisor If true, uses a supervisor job for the child scope.
 * @param context Additional coroutine context elements to be added to the child scope.
 * @return The created child coroutine scope.
 */
fun CoroutineContext.createChildScope(useSupervisor: Boolean, context: CoroutineContext? = null): CoroutineScope {
    val currentScope = CoroutineScope(this)
    return currentScope.createChildScope(useSupervisor, context)
}

/**
 * Invokes a completion handler on the job associated with the coroutine scope.
 *
 * @see Job.invokeOnCompletion
 */
fun CoroutineScope.invokeOnCompletion(handler: CompletionHandler) {
    val job = coroutineContext[Job]!!
    job.invokeOnCompletion(handler)
}

/**
 * Invokes a handler specifically for cancellation events on the job associated with the coroutine scope.
 *
 * @param handler The handler to be invoked upon cancellation.
 */
fun CoroutineScope.invokeOnCancellation(handler: (CancellationException) -> Unit) {
    val job = coroutineContext[Job]!!
    job.invokeOnCompletion {
        if (it is CancellationException) {
            handler(it)
        }
    }
}

/**
 * Launches a coroutine with a cancellation timeout
 *
 * @param context The coroutine context for the block.
 * @param duration The duration of the timeout.
 * @param block The suspendable code block to be executed with a timeout.
 * @return The launched job.
 */
fun CoroutineScope.launchWithTimeout(context: CoroutineContext, duration: Duration, block: suspend CoroutineScope.() -> Unit): Job {
    return launch(CoroutineName("launchWithTimeout")) {
        raceWithCancellation {
            addRacer(CoroutineName("Executable block in launchWithTimeout") + context) { block() }
            addRacer(Dispatchers.IO + CoroutineName("Timeout in launchWithTimeout")) {
                delay(duration)
            }
        }
    }
}

/**
 * Creates a coroutine scope that automatically cancels itself and its children when the block completes.
 *
 * @param block The suspendable code block to be executed within the scope.
 * @return The result of the block execution.
 */
suspend fun <T> coroutineScopeAutoCancel(block: suspend CoroutineScope.() -> T): T {
    val context = EmptyCoroutineContext
    return withContextAutoCancel(context, block, "coroutineScopeAutoCancel")
}


/**
 * Executes a block of code within a specific coroutine context and ensures cancellation of the scope upon completion.
 *
 * @param context The coroutine context for the block execution.
 * @param block The suspendable code block to be executed.
 * @return The result of the block execution.
 */
suspend fun <T> withContextAutoCancel(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
    return withContextAutoCancel(context, block, "withContextAutoCancel")
}

private suspend fun <T> withContextAutoCancel(context: CoroutineContext, block: suspend CoroutineScope.() -> T, name: String): T {
    val currentScope = CoroutineScope(coroutineContext + context)
    return try {
        currentScope.async(CoroutineName(name)) {
            val childrenScope = createChildScope(false, CoroutineName("$name-children"))
            childrenScope.block().also {
                childrenScope.cancel()
            }
        }.await()
    } finally {
        currentScope.cancel()
    }
}