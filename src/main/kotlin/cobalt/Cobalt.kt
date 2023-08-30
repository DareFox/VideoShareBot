package cobalt

import EnvironmentConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ktor
import java.net.URL

object Cobalt {
    suspend fun requestVideo(url: String): String? {
        val response = requestCustom(CobaltRequest(url, isAudioOnly = false))
        return response.url
    }

    suspend fun requestAudio(url: String): String? {
        val response = requestCustom(CobaltRequest(url, isAudioOnly = true))
        return response.audio
    }

    suspend fun requestCustom(request: CobaltRequest): CobaltResponse {
        val response: CobaltResponse = ktor.post {
            url(URL(EnvironmentConfig.cobaltApiUrl, "api/json"))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        if (response.status == CobaltResponseStatus.ERROR) {
            throw CobaltError(response.text ?: "[No error text]")
        }

        return response
    }
}