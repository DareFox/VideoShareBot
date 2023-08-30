package cobalt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PickerItemType {
    @SerialName("video")
    VIDEO,
    @SerialName("photo")
    PHOTO
}