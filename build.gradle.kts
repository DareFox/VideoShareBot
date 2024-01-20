plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.darefox"
version = "1.0-SNAPSHOT"

// Discord API version
val kordExtVersion = "1.6.0"
val http4kVersion = "5.13.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Logger
    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("io.github.oshai:kotlin-logging:5.0.1")

    // http client
    implementation("org.http4k:http4k-client-okhttp:${http4kVersion}")
    implementation("org.http4k:http4k-core:${http4kVersion}")

    // Discord API
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordExtVersion")

    // Result monad
    implementation(platform("dev.forkhandles:forkhandles-bom:2.12.2.0"))
    implementation("dev.forkhandles:result4k")

    // Other
    implementation("me.darefox:cobaltik:1.0.0")
    implementation("commons-io:commons-io:2.15.1")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("MainKt")
}