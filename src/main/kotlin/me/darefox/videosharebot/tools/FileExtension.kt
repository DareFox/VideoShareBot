package me.darefox.videosharebot.tools

class FileExtension(extensionArg: String) {
    val extension: String = if (extensionArg.startsWith(".")) {
        extensionArg.trim()
    } else {
        "." + extensionArg.trim()
    }

    /** Without dot **/
    val suffix: String by lazy { extension.removePrefix(".") }

    init {
        if (extensionArg.startsWith("..")) throw IllegalArgumentException("Extension can't starts with double dots '$extensionArg'")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileExtension

        return extension == other.extension
    }

    override fun hashCode(): Int = extension.hashCode()

    override fun toString(): String = extension
}