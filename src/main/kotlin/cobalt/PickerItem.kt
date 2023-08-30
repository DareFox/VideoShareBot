package cobalt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PickerItem(
    val type: PickerItemType?,
    @SerialName("thumb")
    val thumbnail: String?,
    val url: String
)

