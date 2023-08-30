package cobalt

import kotlinx.serialization.Serializable

@Serializable
enum class VideoCodec {
    h264,
    av1,
    vp9
}