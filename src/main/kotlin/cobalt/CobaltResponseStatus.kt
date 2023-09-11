package cobalt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CobaltResponseStatus {
    @SerialName("error")
    ERROR,

    @SerialName("redirect")
    REDIRECT,

    @SerialName("stream")
    STREAM,

    @SerialName("success")
    SUCCESS,

    @SerialName("rate-limit")
    RATELIMIT,

    @SerialName("picker")
    PICKER
}