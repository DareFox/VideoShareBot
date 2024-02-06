package me.darefox.videosharebot.extensions

import kotlin.time.Duration
import kotlin.time.TimeSource

fun loopFor(duration: Duration, block: (Duration) -> Unit) {
    val start = TimeSource.Monotonic.markNow()
    while (true) {
        val now = TimeSource.Monotonic.markNow()
        val delta = now - start

        if (delta > duration) break
        block(delta)
    }
}