package me.darefox.videosharebot.tools

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration

/**
 * Throttles the invocation of a suspend function with a single argument [T].
 *
 * This class ensures that the provided [function] is not called more frequently
 * than the specified [delayDuration]. It can optionally filter out duplicate
 * arguments based on the [uniqueArguments] flag.
 *
 * @param scope The [CoroutineScope] to launch the throttling coroutine.
 * @param delayDuration The duration to delay between invocations of [function].
 * @param uniqueArguments Flag indicating whether to filter out duplicate arguments.
 * @param function The suspend function to be throttled.
 *
 * @param T The type of the argument passed to the throttled function.
 */
class ThrottledFunction<T> (
    scope: CoroutineScope,
    private val delayDuration: Duration,
    private val uniqueArguments: Boolean,
    private val function: suspend (T) -> Unit
) : (T) -> Unit {
    private val sharedFlow: MutableSharedFlow<T> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    private val collectFlow = if (uniqueArguments) {
        sharedFlow.distinctUntilChanged()
    } else {
        sharedFlow
    }

    private val job = scope.launch(CoroutineName("ThrottledFunction")) { jobLoop() }
    private suspend fun jobLoop() {
        while (true) {
            collectFlow.collect {
                function(it)
                delay(delayDuration)
            }
        }
    }

    override fun invoke(p1: T) {
        sharedFlow.tryEmit(p1)
    }
}


/**
 * Throttles the invocation of a suspend function with no arguments.
 *
 * This class ensures that the provided [function] is not called more frequently
 * than the specified [delayDuration].
 *
 * @param scope The [CoroutineScope] to launch the throttling coroutine.
 * @param delayDuration The duration to delay between invocations of [function].
 * @param function The suspend function to be throttled.
 */
class ThrottledNoArgFunction(
    private val scope: CoroutineScope,
    private val delayDuration: Duration,
    private val function: suspend () -> Unit
) : () -> Unit {
    private val throttledUnit = ThrottledFunction<Unit>(
        scope = scope,
        delayDuration = delayDuration,
        uniqueArguments = false
    ) { function() }

    override fun invoke() {
        throttledUnit.invoke(Unit)
    }
}

/**
 * Creates a throttled version of a suspend function with a single argument [T].
 *
 * @param delayDuration The duration to delay between successive invocations of the function.
 * @param uniqueArguments Flag indicating whether to filter out duplicate arguments.
 * @param function The suspend function to be throttled.
 *
 * @param T The type of the argument passed to the throttled function.
 *
 * @return A throttled version of the provided suspend function.
 */
fun <T> CoroutineScope.throttleFuncArg(
    delayDuration: Duration,
    uniqueArguments: Boolean,
    function: suspend (T) -> Unit
): (T) -> Unit {
    return ThrottledFunction(this, delayDuration, uniqueArguments, function)
}

/**
 * Creates a throttled version of a suspend function with no arguments.
 *
 * @param delayDuration The duration to delay between successive invocations of the function.
 * @param function The suspend function to be throttled.
 *
 * @return A throttled version of the provided suspend function.
 */
fun CoroutineScope.throttleFunc(delayDuration: Duration, function: suspend () -> Unit): () -> Unit {
    return ThrottledNoArgFunction(this, delayDuration, function)
}
