package me.darefox.videosharebot.ffmpeg.encoders

import me.darefox.videosharebot.ffmpeg.*

@FFmpegBuilderMarker
class NvencH264(private val encodeOptions: FFmpegBuilderStep.EncodeOptions) {
    init {
        encodeOptions.codec(Stream(StreamType.VIDEO), "h264_nvenc")
    }

    sealed class Preset(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        data object Default : Preset("default", 0)

        /** hq 2 passes **/
        data object Slow : Preset("slow", 1)

        /** hq 1 pass **/
        data object Medium : Preset("medium", 2)

        /** hp 1 pass **/
        data object Fast : Preset("fast", 3)
        data object HP : Preset("hp", 4)
        data object HQ : Preset("hq", 5)
        data object BD : Preset("bd", 6)
        data object LowLatency : Preset("ll", 7)
        data object LowLatencyHQ : Preset("llhq", 8)
        data object LowLatencyHP : Preset("llhp", 9)
        data object Lossless : Preset("lossless", 10)
        data object LosslessHP : Preset("losslesshp", 11)

        /** fastest (lowest quality */
        data object P1 : Preset("p1", 12)

        /** faster (lower quality) */
        data object P2 : Preset("p2", 13)

        /** fast (low quality) */
        data object P3 : Preset("p3", 14)

        /** medium (default) */
        data object P4 : Preset("p4", 15)

        /** slow (good quality) */
        data object P5 : Preset("p5", 16)

        /** slower (better quality) */
        data object P6 : Preset("p6", 17)

        /** slowest (best quality) */
        data object P7 : Preset("p7", 18)
    }

    fun preset(preset: Preset) = encodeOptions.addSplitOption("-preset", preset.flag)
    fun level(level: String) = encodeOptions.addSplitOption("-level", level)

    sealed class RateControl(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Constant QP mode */
        data object ConstantQP : RateControl("constqp", 0)

        /** Variable bitrate mode */
        data object VBR : RateControl("vbr", 1)

        /** Constant bitrate mode */
        data object CBR : RateControl("cbr", 2)

        /** Variable bitrate mode with MinQP (deprecated) */
        @Deprecated("said by ffmpeg")
        data object VBR_MinQp : RateControl("vbr_minqp", 8388609)

        /**  Multi-pass optimized for image quality (deprecated) */
        @Deprecated("said by ffmpeg")
        data object LL_2Pass_Quality : RateControl("ll_2pass_quality", 8388609)

        /** Multi-pass optimized for constant frame size (deprecated) */
        @Deprecated("said by ffmpeg")
        data object LL_2Pass_Size : RateControl("ll_2pass_size", 8388610)

        /** Multi-pass variable bitrate mode (deprecated) */
        @Deprecated("said by ffmpeg")
        data object VBR_2Pass : RateControl("vbr_2pass", 8388609)

        /** Constant bitrate low delay high quality mode */
        data object CBR_LowDelay_HQ : RateControl("cbr_ld_hq", 8388610)

        /** Constant bitrate high quality mode */
        data object CBR_HQ : RateControl("cbr_hq", 8388610)

        /** Variable bitrate high quality mode */
        data object VBR_HQ : RateControl("vbr_hq", 8388609)
    }

    /** Override the preset rate-control (from -1 to INT_MAX) (default: -1) **/
    fun rateControl(preset: RateControl) = encodeOptions.addSplitOption("-rc", preset.flag)


    sealed class GpuSelect(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Selects which NVENC capable GPU to use. First GPU is 0, second is 1, and so on. **/
        data class Index(val index: Int) : GpuSelect(index.toString(), index)

        /** Pick the first device available **/
        data object Any : GpuSelect("any", -1)

        /** List the available devices **/
        data object List : GpuSelect("list", -2)
    }

    /** Selects which NVENC capable GPU to use. First GPU is 0, second is 1, and so on. **/
    fun gpu(gpuSelect: GpuSelect) = encodeOptions.addSplitOption("-gpu", gpuSelect.code.toString())

    sealed class RgbMode(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Convert to yuv420 **/
        data object YUV420 : RgbMode("yuv420", 1)

        /** Convert to yuv444 **/
        data object YUV444 : RgbMode("yuv444", 2)

        /**  Disables support, throws an error. **/
        data object Disabled : RgbMode("disabled", 0)
    }

    /** Configure how nvenc handles packed RGB input. **/
    fun rgbMode(mode: RgbMode) = encodeOptions.addSplitOption("-rgb_mode", mode.flag)

    sealed class BFrameReferenceMode(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** B frames will not be used for reference **/
        data object Disabled : BFrameReferenceMode("disabled", 0)

        /** Each B frame will be used for reference **/
        data object Each : BFrameReferenceMode("each", 1)

        /** Only (number of B frames)/2 will be used for reference **/
        data object Middle : BFrameReferenceMode("middle", 2)
    }

    /** Use B frames as references **/
    fun bRefMode(mode: BFrameReferenceMode) = encodeOptions.addSplitOption("-b_ref_mode", mode.flag)

    sealed class MultipassMode(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        /** Single Pass **/
        data object Disabled : MultipassMode("disabled", 0)

        /** Two Pass encoding is enabled where first Pass is quarter resolution **/
        data object QuarterResolution : MultipassMode("qres", 1)

        /** Two Pass encoding is enabled where first Pass is full resolution **/
        data object FullResolution : MultipassMode("fullres", 2)
    }

    /** Set the multipass encoding **/
    fun multipass(mode: MultipassMode) = encodeOptions.addSplitOption("-multipass", mode.flag)

    sealed class CoderType(flag: String, code: Int) : FFmpegOptionValue(flag, code) {
        data object Default : CoderType("default", -1)
        data object Auto : CoderType("auto", 0)
        data object Cabac : CoderType("cabac", 1)
        data object Cavlc : CoderType("cavlc", 2)
        data object AC : CoderType("ac", 1)
        data object Vlc : CoderType("vlc", 2)
    }

    /** Coder type, default: default **/
    fun coder(type: CoderType) = encodeOptions.addSplitOption("-coder", type.flag)

    /**  Number of frames to look ahead for rate-control (from 0 to INT_MAX) (default 0) **/
    fun lookAheadForRateControl(frames: Int) = encodeOptions.addSplitOption("-rc-lookhead", frames.toString())

    /** Number of concurrent surfaces (from 0 to 64) (default 0) **/
    fun surfaces(concurrentSurfaces: Int) = encodeOptions.addSplitOption("-surfaces", concurrentSurfaces.toString())

    /** Use cbr encoding mode (default false) **/
    fun useCbr(enabled: Boolean) = encodeOptions.addSplitOption("-cbr", enabled.toString())

    /** Use 2pass encoding mode **/
    fun use2pass(enabled: Boolean) = encodeOptions.addSplitOption("-2pass", enabled.toString())

    /** Delay frame output by the given amount of frames **/
    fun delay(frameAmount: Int) = encodeOptions.addSplitOption("-delay", frameAmount.toString())

    /** When lookahead is enabled, set this to 1 to disable adaptive I-frame insertion at scene cuts (default false) **/
    fun noSceneCut(enabled: Boolean) = encodeOptions.addSplitOption("-no-scenecut", enabled.toString())

    /** If forcing keyframes, force them as IDR frames. (default false) **/
    fun forcedIdr(enabled: Boolean) = encodeOptions.addSplitOption("-forced-idr ", enabled.toString())

    /** When lookahead is enabled, set this to 0 to disable adaptive B-frame decision (default true) **/
    fun bAdapt(enabled: Boolean) = encodeOptions.addSplitOption("-b_adapt", enabled.toString())

    /** set to 1 to enable Spatial AQ (default false) **/
    fun spatialAQ(enabled: Boolean) = encodeOptions.addSplitOption("-spatial-aq", enabled.toString())

    /**  set to 1 to enable Temporal AQ (default false) **/
    fun temporalAQ(enabled: Boolean) = encodeOptions.addSplitOption("-temporal-aq", enabled.toString())

    /**  Set 1 to indicate zero latency operation (no reordering delay) (default false) **/
    fun zeroLatency(enabled: Boolean) = encodeOptions.addSplitOption("-zerolatency", enabled.toString())

    /** Set this to 1 to enable automatic insertion of non-reference P-frames (default false) **/
    fun nonRefP(enabled: Boolean) = encodeOptions.addSplitOption("-nonref_p", enabled.toString())

    /** Set 1 to minimize GOP-to-GOP rate fluctuations (default false) **/
    fun strictGOP(enabled: Boolean) = encodeOptions.addSplitOption("-strict_gop", enabled.toString())

    /** Use access unit delimiters (default false) */
    fun aud(enabled: Boolean) = encodeOptions.addSplitOption("-aud", enabled.toString())

    /** Bluray compatibility workarounds (default false)  */
    fun blurayCompat(enabled: Boolean) = encodeOptions.addSplitOption("-bluray-compat", enabled.toString())

    /** Pass on extra SEI data (e.g. a53 cc) to be included in the bitstream (default true) */
    fun extraSEI(enabled: Boolean) = encodeOptions.addSplitOption("-extra_sei", enabled.toString())

    /** Pass on user data unregistered SEI if available (default false) */
    fun uduSEI(enabled: Boolean) = encodeOptions.addSplitOption("-udu_sei ", enabled.toString())

    /** Use Periodic Intra Refresh instead of IDR frames (default false) */
    fun intraRefresh(enabled: Boolean) = encodeOptions.addSplitOption("-intra-refresh", enabled.toString())

    /** Use A53 Closed Captions (if available) (default true) */
    fun a53cc(enabled: Boolean) = encodeOptions.addSplitOption("-a53cc", enabled.toString())

    /** Enable weighted prediction (default false) **/
    fun weightedPrediction(enabled: Boolean) = encodeOptions.addSplitOption("-weighted_pred", enabled.toString())

    /** Use single slice intra refresh (default false) **/
    fun singleSliceIntraRefresh(enabled: Boolean) =
        encodeOptions.addSplitOption("-single-slice-intra-refresh", enabled.toString())

    /** Enable constrainedFrame encoding where each slice in the constrained picture is independent of other slices (default false) **/
    fun constrainedEncoding(enabled: Boolean) =
        encodeOptions.addSplitOption("-constrained-encoding", enabled.toString())

    /** Set target quality level (0 to 51, 0 means automatic) for constant quality mode in VBR rate control (from 0 to 51) (default 0) **/
    fun cq(value: Float) = encodeOptions.addSplitOption("-cq", value.toString())

    /** When Spatial AQ is enabled, this field is used to specify AQ strength. AQ strength scale is from 1 (low) - 15 (aggressive) (from 1 to 15) (default 8) **/
    fun aqStrength(value: Int) = encodeOptions.addSplitOption("-aq-strength", value.toString())

    /** Initial QP value for P frame (from -1 to 51) (default -1) **/
    fun init_qpP(value: Int) = encodeOptions.addSplitOption("-init_qpP", value.toString())

    /** Initial QP value for B frame (from -1 to 51) (default -1) **/
    fun init_qpB(value: Int) = encodeOptions.addSplitOption("-init_qpB", value.toString())

    /** Initial QP value for I frame (from -1 to 51) (default -1) **/
    fun init_qpI(value: Int) = encodeOptions.addSplitOption("-init_qpI", value.toString())

    /** Constant quantization parameter rate control method (from -1 to 51) (default -1) **/
    fun qp(value: Int) = encodeOptions.addSplitOption("-qp", value.toString())

    /** Quantization parameter offset for cb channel (from -12 to 12) (default 0) **/
    fun qp_CB_Offset(value: Int) = encodeOptions.addSplitOption("-qp_cb_offset", value.toString())

    /** Quantization parameter offset for cr channel (from -12 to 12) (default 0) **/
    fun qp_CR_Offset(value: Int) = encodeOptions.addSplitOption("-qp_cr_offset", value.toString())

    /** Low delay key frame scale; Specifies the Scene Change frame size increase allowed in case of single frame VBV and CBR (from 0 to 255) (default 0) **/
    fun lowDelayKeyFrameScale(value: Int) = encodeOptions.addSplitOption("-ldkfs", value.toString())

    /** Specifies the DPB size used for encoding (0 means automatic) (from 0 to INT_MAX) (default 0) **/
    fun dpbSize(value: Int) = encodeOptions.addSplitOption("-dpb_size", value.toString())

    /** Maximum encoded slice size in bytes (from 0 to INT_MAX) (default 0) **/
    fun maxSliceSize(value: Int) = encodeOptions.addSplitOption("-max_slice_size", value.toString())
}

fun FFmpegBuilderStep.EncodeOptions.nvencH264(builder: NvencH264.() -> Unit) = NvencH264(this).builder()