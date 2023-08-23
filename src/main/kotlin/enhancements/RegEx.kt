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