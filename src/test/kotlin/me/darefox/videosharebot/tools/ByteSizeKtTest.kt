package me.darefox.videosharebot.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class ByteSizeKtTest {

    private val kilobyteData = ByteSizeDataTest(
        byteSize = ByteSize(1000L),
        byteUnit = ByteUnit.Kilobyte,
        leadingZero = false,
        precision = 3,
        shouldBeValue = "1.0",
        shoudlBePrefix = "kB"
    )

    private val kilobyteList = listOf(
        kilobyteData,
        kilobyteData.copy(leadingZero = true, shouldBeValue = "1.000"),
        kilobyteData.copy(byteSize = ByteSize(10000), shouldBeValue = "10.0"),
    )

    private val megabyteList = kilobyteList.map {
        val newSize = ByteSize(it.byteSize.bytes * 1000)
        it.copy(byteUnit = ByteUnit.Megabyte, shoudlBePrefix = "MB", byteSize = newSize)
    }

    private val gigabyteList = megabyteList.map {
        val newSize = ByteSize(it.byteSize.bytes * 1000)
        it.copy(byteUnit = ByteUnit.Gigabyte, shoudlBePrefix = "GB", byteSize = newSize)
    }

    private val terabyteList = gigabyteList.map {
        val newSize = ByteSize(it.byteSize.bytes * 1000)
        it.copy(byteUnit = ByteUnit.Terabyte, shoudlBePrefix = "TB", byteSize = newSize)
    }

    private val allList = listOf(kilobyteList, megabyteList, gigabyteList, terabyteList).flatten()

    @Test
    fun testToString() {
        for (data in allList) {
            data.apply {
                val shouldBe = "$shouldBeValue $shoudlBePrefix"
                val value = byteSize.toString(byteUnit, precision, leadingZero)
                assertEquals(shouldBe, value)
                println("$shouldBe == $value; $data")
            }
        }
    }
}

private data class ByteSizeDataTest(
    val byteSize: ByteSize,
    val byteUnit: ByteUnit,
    val leadingZero: Boolean,
    val precision: Int,
    val shouldBeValue: String,
    val shoudlBePrefix: String,
)