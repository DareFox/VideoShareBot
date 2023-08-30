package cobalt

import kotlinx.serialization.SerialName

data class PickerItem(
    val type: PickerItemType?,
    @SerialName("thumb")
    val thumbnail: String?,
    val url: String
)

