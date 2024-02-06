package me.darefox.videosharebot.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val KtorHttp = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = Long.MAX_VALUE;
        connectTimeoutMillis = 15000;
        socketTimeoutMillis = 10000;
    }
}