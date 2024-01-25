package me.darefox.videosharebot.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class ByteSizeKtTest {

    private val baseData = ByteSizeDataTest(
        byteSize = ByteSize(1000L),
        byteUnit = ByteUnit.Kilobyte,
        leadingZero = false,
        precision = 3,
        shouldBe = "1.0 kB"
    )

    private val list = listOf(
        baseData,
        baseData.copy(leadingZero = true, shouldBe = "1.000 kB"),
        baseData.copy(byteSize = ByteSize(10000), shouldBe = "10.0 kB"),`
    )
    @Test
    fun testToString() {
        for (data in list) {
            data.apply {
                assertEquals(shouldBe, byteSize.toString(byteUnit, precision, leadingZero))
            }
        }
    }
}

private data class ByteSizeDataTest(
    val byteSize: ByteSize,
    val byteUnit: ByteUnit,
    val leadingZero: Boolean,
    val precision: Int,
    val shouldBe: String,
)