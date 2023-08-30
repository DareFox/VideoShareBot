package cobalt

data class CobaltError(val text: String) : Error(text)