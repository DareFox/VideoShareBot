package me.darefox.videosharebot.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FilterMode {
    @SerialName("all")
    ALL,
    @SerialName("whitelist")
    WHITELIST,
    @SerialName("blacklist")
    BLACKLIST
}

@Serializable
data class FilterRule<T>(
    val mode: FilterMode,
    val values: Set<T>
)

fun <T> Collection<T>.filter(filterRule: FilterRule<T>): List<T> {
    return this.filter { filterRule.isAllowed(it) }
}

fun <T> FilterRule<T>.isAllowed(value: T): Boolean {
    return when (mode) {
        FilterMode.ALL -> true
        FilterMode.WHITELIST -> value in values
        FilterMode.BLACKLIST -> value !in values
    }
}



fun <T> FilterRule<T>.isNotAllowed(value: T): Boolean = !isAllowed(value)