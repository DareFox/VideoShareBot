package me.darefox.videosharebot.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface Toggleable {
    @SerialName("enabled")
    val isEnabled: Boolean
}


@OptIn(ExperimentalContracts::class)
fun isEnabled(toggleable: Toggleable?): Boolean {
    contract {
        returns(true) implies (toggleable is Toggleable)
    }
    return toggleable?.isEnabled ?: false
}