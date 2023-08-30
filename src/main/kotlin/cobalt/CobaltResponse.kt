package cobalt

data class CobaltResponse(
    val status: CobaltResponseStatus,
    val url: String?,
    val text: String?,
    val pickerType: PickerType?,
    val picker: List<PickerItem>?,
//    val audiom
)

