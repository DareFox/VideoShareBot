package cobalt

import kotlinx.serialization.Serializable
import serialization.MixedNullableString

@Serializable
data class CobaltResponse(
    val status: CobaltResponseStatus,
    val url: String?,
    val text: String?,
    val pickerType: PickerType?,
    val picker: List<PickerItem>?,
    @Serializable(with = MixedNullableString::class)
    val audio: String?
)

