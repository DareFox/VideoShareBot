package me.darefox.videosharebot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val ktor = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 45000;
        connectTimeoutMillis = 10000;
    }
}