package me.darefox.videosharebot.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class MathKtTest {

    val maps = mapOf(
        4 to pow(2,2),
        1_000_000 to pow(1000,2),
        16 to pow(4,2)
    )
    @Test
    fun pow() {
        for ((key, value) in maps) {
            assertEquals(key.toLong(), value)
        }
    }
}