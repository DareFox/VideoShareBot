package me.darefox.videosharebot.config

import me.darefox.videosharebot.config.impl.YamlConfig
import java.io.File

val GlobalApplicationConfig = YamlConfig.read(File("config.yaml"))