package enhancements

/**
 * Extension function for converting a sequence of [MatchResult] objects into a list of matched values.
 *
 * @receiver The sequence of [MatchResult] objects.
 * @return A list of strings containing the matched values from the [MatchResult] objects.
 */
fun Sequence<MatchResult>.toValueList(): List<String> {
    return this.toList().map {
        it.value
    }
}



/**
 * Extension function for converting a list of string patterns into a list of regular expressions with specified options.
 *
 * @receiver The list of string patterns to be converted.
 * @param options The set of [RegexOption] to be applied to the regular expressions.
 * @return A list of [Regex] objects corresponding to the converted string patterns with the specified options.
 */
fun List<String>.toRegex(options: Set<RegexOption>): List<Regex> {
    return this.map {
        it.toRegex(options)
    }
}

/**
 * Extension function for converting a list of string patterns into a list of regular expressions with a single option.
 *
 * @receiver The list of string patterns to be converted.
 * @param option The single [RegexOption] to be applied to the regular expressions.
 * @return A list of [Regex] objects corresponding to the converted string patterns with the specified option.
 */
fun List<String>.toRegex(option: RegexOption): List<Regex> {
    return this.map {
        it.toRegex(option)
    }
}

/**
 * Extension function for converting a list of string patterns into a list of regular expressions.
 *
 * @receiver The list of string patterns to be converted.
 * @return A list of [Regex] objects corresponding to the converted string patterns.
 */
fun List<String>.toRegex(): List<Regex> {
    return this.map {
        it.toRegex()
    }
}