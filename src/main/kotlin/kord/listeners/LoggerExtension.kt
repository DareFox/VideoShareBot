package kord.listeners

import com.kotlindiscord.kord.extensions.extensions.Extension
import io.github.oshai.kotlinlogging.KotlinLogging

abstract class LoggerExtension(name: String) : Extension() {
    override val name: String = name
    protected val log = KotlinLogging.logger(name)
}