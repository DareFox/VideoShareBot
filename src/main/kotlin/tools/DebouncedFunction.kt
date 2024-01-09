package tools

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Duration

class DebouncedFunction<T>(private val delayDuration: Duration, private val function: suspend (T) -> Unit) {
    private val scope = CoroutineScope(context = Dispatchers.Default)
    private val mutex = Mutex()

    private var lastValue: T? = null
        set(value) {
            field = value
            assigned = true
        }

    private var assigned = false
    private var delayedJob: Job? = null

    @Suppress("UNCHECKED_CAST")
    private fun launchDelayJob() {
        val mutexLocked = mutex.tryLock()
        try {
            if (mutexLocked && delayedJob?.isCompleted != false && assigned) {
                delayedJob = scope.launch {
                    val value = lastValue as T
                    callFunction(value)
                    delay(delayDuration)
                }
            }
        } finally {
            if (mutexLocked) mutex.unlock()
        }

    }

    private suspend fun callFunction(arg: T) {
        function(arg)
    }

    fun run(arg: T) {
        lastValue = arg
        launchDelayJob()
    }
}