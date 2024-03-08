package me.darefox.videosharebot.config.impl

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.decodeFromStream
import me.darefox.videosharebot.config.ApplicationConfig
import me.darefox.videosharebot.extensions.censorContains
import me.darefox.videosharebot.extensions.createLogger
import java.io.File

object YamlConfig {
    private val log = createLogger()
    private val yaml = Yaml(
        configuration = YamlConfiguration(
            polymorphismStyle = PolymorphismStyle.Property
        )
    )

    fun read(file: File): ApplicationConfig {
        require(file.exists()) { "Config file doesn't exist" }
        require(file.isFile) { "Config is not a file.${if (file.isDirectory) " It is directory" else "" }" }
        log.debug { "Opening stream" }
        file.inputStream().use { stream ->
            log.debug { "Parsing stream" }
            val config = yaml.decodeFromStream<ApplicationConfig>(stream)
            log.debug { config.toString().censorContains(config.discord.token) }
            return config
        }
    }
}