package cobalt

import kotlinx.serialization.SerialName

enum class PickerItemType {
    @SerialName("video")
    VIDEO,
    @SerialName("photo")
    PHOTO
}