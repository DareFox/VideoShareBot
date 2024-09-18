plugins {
    application
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.10"
    id("com.gradleup.shadow") version "8.3.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

group = "me.darefox"
version = "1.0-SNAPSHOT"

val kordExtVersion = "1.6.0"
val ktorVersion = "2.3.12"
val kotest = "5.9.1"

repositories {
    mavenCentral()
}

detekt {
    config.setFrom((file("detekt.yml")))
    parallel = true
}

dependencies {
    testImplementation(kotlin("test"))

    // Discord API
    implementation("dev.kord:kord-core:0.14.0")
    // implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordExtVersion")

    // Cobalt API
    implementation("me.darefox:cobaltik:2.0.0")

    // IO Libs
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.1")
    implementation("commons-io:commons-io:2.16.1")

    // Logger
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.oshai:kotlin-logging:7.0.0")

    // Result monad
    implementation(platform("dev.forkhandles:forkhandles-bom:2.19.0.0"))
    implementation("dev.forkhandles:result4k")

    // ktor client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // YAML serializer
    implementation("com.charleskorn.kaml:kaml:0.61.0")

    // Test libraries
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest")
    testImplementation("io.kotest:kotest-property:$kotest")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(22)
}

application {
    mainClass.set("me.darefox.videosharebot.MainKt")
}
