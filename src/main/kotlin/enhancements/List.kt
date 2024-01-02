package enhancements

/**
 *  Shortcut for `listOf(listOf(...))`
 */
fun <T> nestedListOf(vararg elements: T): List<List<T>> {
    return listOf(elements.toList())
}