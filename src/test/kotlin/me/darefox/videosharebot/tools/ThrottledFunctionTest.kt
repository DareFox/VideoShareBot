package me.darefox.videosharebot.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

class ThrottledFunctionTest {
    private val delays = listOf(1, 10, 50, 100, 200).map { it.milliseconds }
    private val scope = CoroutineScope(Dispatchers.Default)

    private val maxErrorMargin: Duration = runBlocking {
        val delayAtLeast = 1L
        (0..<40).maxOf {
            val delayTime = measureTime { delay(delayAtLeast) }
            delayTime - delayAtLeast.toDuration(DurationUnit.MILLISECONDS)
        }.plus(1.milliseconds)
    }

    init {
        println("Max error margin: $maxErrorMargin")
    }

    private fun assertInErrorMargin(shouldDelay: Duration, delayedFor: Duration) {
        val extraDelay = delayedFor - shouldDelay
        println("Should delay: $shouldDelay; Delayed for $delayedFor; Extra delay (delayedFor - shouldDelay): $extraDelay")
        assert(extraDelay < maxErrorMargin) {
            "delay($shouldDelay) call delayed for $delayedFor with a bigger error margin: \n Extra delay ($extraDelay) > Maximum error margin ($maxErrorMargin)"
        }
    }
    data class ThrottleCreateData(
        val shouldCall: suspend () -> Unit,
        val throttleDelay: Duration,
    )

    private fun <T> testErrorMargin(
        create: (ThrottleCreateData) -> T,
        call: (T) -> Unit
    ) {
        for (throttleDelay in delays) {
            var previousCall = TimeSource.Monotonic.markNow()
            val lastCall = MutableStateFlow<TimeSource.Monotonic.ValueTimeMark?>(null)

            var counter = 0
            val shouldCall = suspend {
                if (counter >= 3) /* warmup */ {
                    if (lastCall.value == null)
                        lastCall.value = TimeSource.Monotonic.markNow()
                } else {
                    counter++
                    previousCall = TimeSource.Monotonic.markNow()
                }
            }

            val func = create(ThrottleCreateData(
                shouldCall = shouldCall,
                throttleDelay = throttleDelay
            ))

            while (true) {
                call(func)
                val time = lastCall.value
                if (time != null) {
                    val delayedFor = time - previousCall
                    assertInErrorMargin(throttleDelay, delayedFor)
                    break
                }
            }
        }
    }

    @Test
    fun `Delay time of empty argument function should be in error margin`() {
        testErrorMargin(
            create = { (shouldCall, delay) -> scope.throttleFunc(
                delayDuration = delay,
                delayMode = DelayMode.NORMAL_DELAY,
                function = shouldCall
            ) },
            call = { func -> func() }
        )
    }

    @Test
    fun `Delay time of function with ONLY_UNIQUE_ARGUMENTS flag should be in error margin`() {
        var counter = 0
        testErrorMargin(
            create = { (shouldCall, delay) ->
                scope.throttleFuncArg<Int>(
                    delayDuration = delay,
                    argumentsMode = ArgumentsMode.ONLY_UNIQUE_ARGUMENTS,
                    delayMode = DelayMode.NORMAL_DELAY
                ) {
                    shouldCall()
                    counter++
                }
            },
            call = { func -> func(counter) },
        )
    }

    @Test
    fun `Delay time of function ANY_ARGUMENTS should be in error margin`() {
        testErrorMargin(
            create = { (shouldCall, delay) ->
                scope.throttleFuncArg<Unit>(
                    delay,
                    argumentsMode = ArgumentsMode.ANY_ARGUMENTS,
                    delayMode = DelayMode.NORMAL_DELAY
                ) {
                    shouldCall()
                }
            },
            call = { func -> func(Unit) },
        )
    }

    @Test
    fun `Throttled function with ONLY_UNIQUE_ARGUMENTS flag should filter out same values`() {
        val notUnique = mutableListOf(0, 0, 1, 2, 3, 4, 4, 42, 42, 123, 123, 222_222, 222_222, 96)
        val uniqueList = notUnique.toSet().toList()
        val actualList = mutableListOf<Int>()

        val func = scope.throttleFuncArg<Int>(
            delayDuration = 1.milliseconds,
            delayMode = DelayMode.DELAY_MINUS_PROCESS_TIME,
            argumentsMode = ArgumentsMode.ONLY_UNIQUE_ARGUMENTS
        ) {
            actualList += it
        }

        for (value in notUnique) {
            func(value)
            runBlocking { delay(10.milliseconds + maxErrorMargin) }
        }

        assertContentEquals(
            expected = uniqueList,
            actual = actualList
        )
    }

    private fun testOnlyUniqueValues(argumentFlag: ArgumentsMode) {
        val shouldHaveList = mutableListOf<Int>()
        val actualList = mutableListOf<Int>()

        val func = scope.throttleFuncArg<Int>(
            1.milliseconds,
            delayMode = DelayMode.DELAY_MINUS_PROCESS_TIME,
            argumentsMode = argumentFlag
        ) {
            actualList += it
        }

        for (i in 1..10) {
            func(i)
            shouldHaveList += i
            runBlocking { delay(10.milliseconds + maxErrorMargin) }
        }

        assertContentEquals(
            expected = shouldHaveList,
            actual = actualList
        )
    }

    @Test
    fun `Throttled function with ONLY_UNIQUE_ARGUMENTS flag should accepts unique values`() {
        testOnlyUniqueValues(ArgumentsMode.ONLY_UNIQUE_ARGUMENTS)
    }

    @Test
    fun `Throttled function with ANY_ARGUMENTS flag should accepts all values`() {
        testOnlyUniqueValues(ArgumentsMode.ANY_ARGUMENTS)
    }
}