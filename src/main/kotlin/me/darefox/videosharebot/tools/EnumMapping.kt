package me.darefox.videosharebot.tools

interface EnumMapping<T> {
    val mappedValue: T
}

fun <T> List<EnumMapping<T>>.toValues(): List<T> {
    return map { it.mappedValue }
}
