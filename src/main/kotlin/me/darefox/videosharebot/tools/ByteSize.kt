package me.darefox.videosharebot.tools

import me.darefox.videosharebot.extensions.pow
import java.math.RoundingMode.DOWN
import java.text.DecimalFormat

private const val DECIMAL_BASE = 1000

data class ByteSize(
    val bytes: Long
) {
    override fun toString(): String = "$bytes B"
}

enum class ByteUnit(val conversionRateInBytes: Long, val suffix: String) {
    Kilobyte(DECIMAL_BASE.toLong(), "kB"),
    Megabyte(pow(DECIMAL_BASE, 2), "MB"),
    Gigabyte(pow(DECIMAL_BASE, 3), "GB"),
    Terabyte(pow(DECIMAL_BASE, 4), "TB")
}

fun ByteSize.toString(
    unit: ByteUnit,
    precision: Int = 3,
    leadingZeros: Boolean = false
): String {
    require(precision >= 0) { "Precision cannot be negative" }

    val afterDotSymbol = if (leadingZeros) '0' else '#'
    val pattern = buildString {
        append("0.0")
        repeat(precision - 1) { append(afterDotSymbol) }
    }

    val decimalFormat = DecimalFormat(pattern)
    decimalFormat.roundingMode = DOWN

    val result = decimalFormat.format(bytes.toDouble() / unit.conversionRateInBytes).replace(',', '.')
    return "$result ${unit.suffix}"
}