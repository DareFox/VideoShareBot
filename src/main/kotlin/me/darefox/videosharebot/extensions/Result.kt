package me.darefox.videosharebot.extensions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

/** Type alias for [Result] to simplify importing it (Kotlin, Ktor and Result4k provide same class name for this monad) */
typealias ResultMonad<Val,Err> = Result<Val,Err>

/** Return empty [Success] **/
fun Success() = Success(Unit)

/**
 * Executes the provided [block] and wraps the result in a [ResultMonad].
 *
 * @param block The code block to be executed, producing a result of type [Val].
 * @return A [ResultMonad] representing the result of the operation.
 *   - If the block execution is successful, returns a [Success] with the result.
 *   - If an exception of type [Err] is caught during execution, returns a [Failure] with the caught exception.
 *   - If an exception of a different type is caught, rethrows the exception.
 *
 * @param Val The type of the result produced by the [block].
 * @param Err The type of exception that the function is expected to handle.
 * @return A [ResultMonad] encapsulating the result or exception.
 *
 * @throws Exception If an exception of a type other than [Err] is caught during execution, it is rethrown.
 */
inline fun <Val, reified Err: Exception> tryAsResult(block: () -> Val): ResultMonad<Val, Err> {
    return try {
        Success(block())
    } catch (e: Exception) {
        if (e is Err) {
            Failure(e)
        } else {
            throw e
        }
    }
}

/**
 * Applies the provided transformation functions based on the result type of the [ResultMonad].
 *
 * @param ifSuccess A lambda expression to be applied if the [ResultMonad] is a [Success].
 *                   The lambda receives a [Success] instance and returns a result of type [R].
 * @param ifFailure A lambda expression to be applied if the [ResultMonad] is a [Failure].
 *                   The lambda receives a [Failure] instance and returns a result of type [R].
 * @return The result of applying the appropriate transformation based on the [ResultMonad] type.
 *
 * @param Val The type of the value encapsulated by [Success].
 * @param Err The type of the error encapsulated by [Failure].
 * @param R The type of the result produced by the transformation functions.
 * @return The result of applying the transformation functions.
 */
inline fun <Val, Err, R> ResultMonad<Val, Err>.fold(ifSuccess: Success<Val>.() -> R, ifFailure: Failure<Err>.() -> R): R {
    return when(this) {
        is Failure -> ifFailure(this)
        is Success -> ifSuccess(this)
    }
}