package me.darefox.videosharebot.ffmpeg

@FFmpegBuilderMarker
sealed class FFmpegBuilderStep {
    data class FFmpegArgument(
        val splitArgument: List<String>
    )

    private val args = mutableListOf<FFmpegArgument>()
    private fun flattenArguments(args: List<FFmpegArgument>) = args.map {
        it.splitArgument
    }.flatten()

    private fun addToList(list: List<String>) {
        args += FFmpegArgument(list)
    }


    fun addSplitOption(vararg splitOption: String) {
        addToList(splitOption.toList())
    }

    fun addSplitOption(splitOption: List<String>) {
        addToList(splitOption)
    }

    infix fun FFmpegBuilderStep.addOption(option: String) {
        addToList(option.trim().split(" "))
    }

    operator fun String.unaryPlus() {
        addOption(this)
    }

    fun build(): List<String> = flattenArguments(args)

    sealed class DecodeAndEncodeOptions : FFmpegBuilderStep()
    class DecodeOptions : DecodeAndEncodeOptions()
    class EncodeOptions : DecodeAndEncodeOptions()
    class GlobalOptions : FFmpegBuilderStep()
}