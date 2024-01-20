package extensions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

/** Type alias for [Result] to simplify importing it (Kotlin, Ktor and Result4k provide same class name for this monad) */
typealias ResultMonad<T,E> = Result<T,E>

/**
 * Executes the provided [block] and wraps the result in a [ResultMonad].
 *
 * @param block The code block to be executed, producing a result of type [T].
 * @return A [ResultMonad] representing the result of the operation.
 *   - If the block execution is successful, returns a [Success] with the result.
 *   - If an exception of type [E] is caught during execution, returns a [Failure] with the caught exception.
 *   - If an exception of a different type is caught, rethrows the exception.
 *
 * @param T The type of the result produced by the [block].
 * @param E The type of exception that the function is expected to handle.
 * @return A [ResultMonad] encapsulating the result or exception.
 *
 * @throws Exception If an exception of a type other than [E] is caught during execution, it is rethrown.
 */
inline fun <T, reified E: Exception> tryAsResult(block: () -> T): ResultMonad<T,E> {
    return try {
        Success(block())
    } catch (e: Exception) {
        if (e is E) {
            Failure(e)
        } else {
            throw e
        }
    }
}