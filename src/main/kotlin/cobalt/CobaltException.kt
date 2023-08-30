package cobalt

data class CobaltException(val text: String) : Error(text)