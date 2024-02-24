package me.darefox.videosharebot.extensions

@OptIn(ExperimentalStdlibApi::class)
fun Any.hashCodeHex(): String = this.hashCode().toHexString()