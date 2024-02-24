package me.darefox.videosharebot.ffmpeg.encoders

import me.darefox.videosharebot.ffmpeg.*

@FFmpegBuilderMarker
class Libopus(private val encodeOptions: FFmpegBuilderStep.EncodeOptions) {
    init {
        encodeOptions.codec(Stream(StreamType.AUDIO), "libopus")
    }

    sealed class ApplicationType(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Favor improved speech intelligibility **/
        data object VOIP: ApplicationType("voip", 2048)
        /** Favor faithfulness to the input **/
        data object Audio: ApplicationType("audio", 2049)
        /** Restrict to only the lowest delay modes, disable voice-optimized modes **/
        data object LowDelay: ApplicationType("lowdelay", 2051)
    }

    /** Intended application type (from 2048 to 2051) (default audio) **/
    fun application(type: ApplicationType) = encodeOptions.addSplitOption("-application", type.flag)

    sealed class VbrMode(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Use constant bit rate **/
        data object Off: VbrMode("off", 0)
        /** Use variable bit rate **/
        data object On: VbrMode("on", 1)
        /** Use constrained VBR **/
        data object Constrained: VbrMode("constrained", 2)
    }

    /** Variable bit rate mode (from 0 to 2) (default on) **/
    fun vbr(mode: VbrMode) = encodeOptions.addSplitOption("-vbr", mode.flag)

    /** Duration of a frame in milliseconds (from 2.5 to 120) (default 20) **/
    fun frameDuration(milliseconds: Float) = encodeOptions.addSplitOption("-frame_duration", milliseconds.toString())
    /** Channel Mapping Family (from -1 to 255) (default -1) **/
    fun mappingFamily(value: Int) = encodeOptions.addSplitOption("-mapping_family", value.toString())
    /** Expected packet loss percentage (from 0 to 100) (default 0) **/
    fun packetLoss(value: Int) = encodeOptions.addSplitOption("-packet_loss", value.toString())
    /** Apply intensity stereo phase inversion (default true) **/
    fun applyPhaseInversion(enabled: Boolean) = encodeOptions.addSplitOption("-apply_phase_inv", enabled.toString())
    /** Enable inband FEC. Expected packet loss must be non-zero (default false) **/
    fun inbandFec(enabled: Boolean) = encodeOptions.addSplitOption("-fec", enabled.toString())
}

fun FFmpegBuilderStep.EncodeOptions.libopus(builder: Libopus.() -> Unit) = Libopus(this).builder()