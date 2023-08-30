package cobalt

import kotlinx.serialization.SerialName

enum class VideoQuality() {
    @SerialName("144")
    _144p,
    @SerialName("240")
    _240p,
    @SerialName("360")
    _360p,
    @SerialName("480")
    _480p,
    @SerialName("720")
    _720p,
    @SerialName("1080")
    _1080p,
    @SerialName("1440")
    _1440p,
    @SerialName("2160")
    _2160p,
    @SerialName("max")
    MAX,
}