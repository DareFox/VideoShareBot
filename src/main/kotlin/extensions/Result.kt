package extensions

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success

typealias ResultMonad<T,E> = Result<T,E>
typealias StringErrorResult = ResultMonad<String, String>

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