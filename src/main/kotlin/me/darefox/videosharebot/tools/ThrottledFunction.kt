package me.darefox.videosharebot.tools

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Enum representing different delay modes for throttled function invocations.
 *
 * @see NORMAL_DELAY
 * @see DELAY_MINUS_PROCESS_TIME
 */
enum class DelayMode {
    /**
     * Normal delay mode, where after each invocation, function is delayed by the specified duration.
     */
    NORMAL_DELAY,

    /**
     * Delay mode that subtracts the execution time of the function from the specified delay duration.
     *
     * For example: In this mode if the function finishes in 10 seconds and the delay was set to 15 seconds,
     * then function will be delayed to 5 seconds because:
     *
     * `newDelay = (delay - func) = (15s - 10s) = 5s`
     */
    DELAY_MINUS_PROCESS_TIME
}

/**
 * Enum representing different argument modes for throttled function invocations.
 *
 * @see ONLY_UNIQUE_ARGUMENTS
 * @see ANY_ARGUMENTS
 */
enum class ArgumentsMode {
    /**
     * Only unique arguments mode, where duplicate arguments are filtered out,
     * and the function is invoked only with unique argument values.
     */
    ONLY_UNIQUE_ARGUMENTS,

    /**
     * Any arguments mode, where all arguments are considered, regardless of uniqueness.
     * The function is invoked with every provided argument.
     */
    ANY_ARGUMENTS
}

/**
 * Throttles the invocation of a suspend function with a single argument [T].
 *
 * This class ensures that the provided [function] is not called more frequently
 * than the specified [delayDuration] and [delayMode]. It can optionally filter out duplicate
 * arguments based on the [argumentsMode] flag.
 *
 * @param scope The [CoroutineScope] to launch the throttling coroutine.
 * @param delayDuration The duration to delay between invocations of [function].
 * @param argumentsMode The mode for handling function arguments.
 * @param delayMode The mode for handling delays between invocations.
 * @param function The suspend function to be throttled.
 *
 * @param T The type of the argument passed to the throttled function.
 */
class ThrottledFunction<T> (
    scope: CoroutineScope,
    private val delayDuration: Duration,
    private val argumentsMode: ArgumentsMode,
    private val delayMode: DelayMode,
    private val function: suspend (T) -> Unit
) : (T) -> Unit {
    private val sharedFlow = MutableSharedFlow<T>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val collectFlow = when (argumentsMode) {
        ArgumentsMode.ONLY_UNIQUE_ARGUMENTS -> sharedFlow.distinctUntilChanged()
        ArgumentsMode.ANY_ARGUMENTS -> sharedFlow
    }

    private val job = scope.launch(CoroutineName("ThrottledFunction")) { jobLoop() }
    private suspend fun jobLoop() {
        while (true) {
            collectFlow.collect {
                when(delayMode) {
                    DelayMode.NORMAL_DELAY -> {
                        function(it)
                        delay(delayDuration)
                    }
                    DelayMode.DELAY_MINUS_PROCESS_TIME -> {
                        val processTime = measureTime { function(it) }
                        delay(delayDuration - processTime)
                    }
                }

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
 * than the specified [delayDuration] and [delayMode].
 *
 * @param scope The [CoroutineScope] in which the throttled function operates.
 * @param delayDuration The duration to delay each invocation by.
 * @param delayMode The mode for handling delays between invocations.
 * @param function The suspend function with no arguments to be invoked.
 */
class ThrottledNoArgFunction(
    private val scope: CoroutineScope,
    private val delayDuration: Duration,
    private val delayMode: DelayMode,
    private val function: suspend () -> Unit
) : () -> Unit {
    private val throttledUnit = ThrottledFunction<Unit>(
        scope = scope,
        delayDuration = delayDuration,
        argumentsMode = ArgumentsMode.ANY_ARGUMENTS,
        delayMode = delayMode
    ) { function() }

    override fun invoke() {
        throttledUnit.invoke(Unit)
    }
}

/**
 * Creates a throttled version of a suspend function with a single argument [T].
 *
 * @param delayDuration The duration to delay each invocation by.
 * @param delayMode The mode for handling delays between invocations.
 * @param argumentsMode The mode for handling function arguments.
 * @param function The suspend function to be invoked
 * @param T The type of the argument passed to the throttled function.
 *
 * @return A throttled version of the provided suspend function.
 */
fun <T> CoroutineScope.throttleFuncArg(
    delayDuration: Duration,
    delayMode: DelayMode,
    argumentsMode: ArgumentsMode = ArgumentsMode.ANY_ARGUMENTS,
    function: suspend (T) -> Unit
): (T) -> Unit {
    return ThrottledFunction(this, delayDuration, argumentsMode, delayMode, function)
}

/**
 * Creates a throttled version of a suspend function with no arguments.
 *
 * @param delayDuration The duration to delay each invocation by.
 * @param delayMode The mode for handling delays between invocations.
 * @param function The suspend function with no arguments to be invoked.
 *
 * @return A throttled version of the provided suspend function.
 */
fun CoroutineScope.throttleFunc(delayDuration: Duration, delayMode: DelayMode, function: suspend () -> Unit): () -> Unit {
    return ThrottledNoArgFunction(this, delayDuration, delayMode, function)
}
