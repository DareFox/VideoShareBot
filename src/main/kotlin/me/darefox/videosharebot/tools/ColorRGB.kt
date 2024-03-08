package me.darefox.videosharebot.tools

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ColorRGBSerializer::class)
data class ColorRGB(
    val red: UByte,
    val green: UByte,
    val blue: UByte
) {
    companion object {
        fun fromHex(hexCode: String): ColorRGB {
            val clean = hexCode.removePrefix("#")
            require(clean.length % 2 == 0) {
                "Hex code '$clean' is not even"
            }
            require(clean.length == 6) {
                "Hex code '$clean' is not triplet. e.g.: AABBCC"
            }
            val (red, green, blue) = try {
                clean.chunked(2).map {
                    it.toInt(16).toUByte()
                }
            } catch (e: NumberFormatException) {
                throw NumberFormatException("Wrong hex format.${e.message?.let { " " + e.message }}")
            }
            return ColorRGB(red, green, blue)
        }
    }

    override fun toString(): String {
        val hex = listOf(red, green, blue).joinToString(separator = "") {
            it.toString(16)
        }
        return "#$hex"
    }
}

object ColorRGBSerializer: KSerializer<ColorRGB> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(this.javaClass.name, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ColorRGB {
        return ColorRGB.fromHex(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: ColorRGB) {
        encoder.encodeString(value.toString())
    }
}