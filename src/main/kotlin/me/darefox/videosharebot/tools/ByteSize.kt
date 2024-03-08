package me.darefox.videosharebot.tools

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.darefox.videosharebot.extensions.pow
import java.math.RoundingMode.DOWN
import java.text.DecimalFormat

private const val DECIMAL_BASE = 1000

@Serializable(with = ByteSizeSerializer::class)
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

object ByteSizeSerializer: KSerializer<ByteSize> {
    private val amountRegex = "^\\d+".toRegex()
    private val unitRegex = "(?<=(\\d| ))[A-z]+".toRegex()
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(javaClass.name, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ByteSize {
        val value = decoder.decodeString().trim()
        val amount = amountRegex.find(value)?.value?.toLong() ?: throw IllegalArgumentException("Wrong format for byte size")
        val unit = unitRegex.find(value)?.value ?: throw IllegalArgumentException("Prefix for unit byte measurement not found")

        return when (unit.lowercase()) {
            "b" -> ByteSize(amount)
            "kb" -> amount.toInt().toKilobyte()
            "mb" -> amount.toInt().toMegabyte()
            "gb" -> amount.toInt().toGigabyte()
            "tb" -> amount.toInt().toTerabyte()
            else -> throw IllegalArgumentException("$unit is not supported")
        }
    }

    override fun serialize(encoder: Encoder, value: ByteSize) {
        encoder.encodeString("${value.bytes} B")
    }
}

fun Long.toByteSize() = ByteSize(this)
fun Int.toKilobyte() = ByteSize(ByteUnit.Kilobyte.conversionRateInBytes * this)
fun Int.toMegabyte() = ByteSize(ByteUnit.Megabyte.conversionRateInBytes * this)
fun Int.toGigabyte() = ByteSize(ByteUnit.Gigabyte.conversionRateInBytes * this)
fun Int.toTerabyte() = ByteSize(ByteUnit.Terabyte.conversionRateInBytes * this)