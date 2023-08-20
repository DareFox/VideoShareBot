plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.darefox"
version = "1.0-SNAPSHOT"

// Discord API version
val kordExtVersion = "1.5.6"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Logger
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")

    // Discord API
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordExtVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}