plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.darefox"
version = "1.0-SNAPSHOT"

val kordExtVersion = "1.6.0"
val ktorVersion = "2.3.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Kotlinx IO
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.1")

    // Logger
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.oshai:kotlin-logging:5.0.1")

    // Discord API
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordExtVersion")

    // Result monad
    implementation(platform("dev.forkhandles:forkhandles-bom:2.12.2.0"))
    implementation("dev.forkhandles:result4k")

    // Other
    implementation("me.darefox:cobaltik:1.0.0")
    implementation("commons-io:commons-io:2.15.1")

    // ktor client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // YAML serializer
    implementation("com.charleskorn.kaml:kaml:0.57.0")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("me.darefox.videosharebot.MainKt")
}