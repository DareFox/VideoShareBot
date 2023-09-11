package cobalt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PickerType {
    @SerialName("various")
    VARIOUS,

    @SerialName("images")
    IMAGES
}