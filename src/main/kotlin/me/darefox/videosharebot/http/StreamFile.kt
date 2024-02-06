package me.darefox.videosharebot.http

import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun <T> requestFile(getUrl: String, streamProcess: suspend (HttpResponse) -> T): T {
    return KtorHttp.prepareGet(getUrl).execute(streamProcess)
}