package me.darefox.videosharebot.extensions

/**
 * Calculates the hexadecimal representation of the hash code of this object.
 */
@OptIn(ExperimentalStdlibApi::class)
fun Any.hashCodeHex(): String = this.hashCode().toHexString()