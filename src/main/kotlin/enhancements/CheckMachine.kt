package enhancements

class CheckMachine(val states: Map<String, Boolean>) {
    constructor(states: List<Pair<String,Boolean>>): this(states.toMap())
    constructor(vararg states: Pair<String,Boolean>): this(states.toMap())

    val falsePairs by lazy { states.entries.filter { !it.value }.map { it.toPair() } }

    val truePairs by lazy { states.entries.filter { it.value }.map { it.toPair() } }

    val isAllFalse by lazy { states.all { !it.value } }

    val isAllTrue by lazy { states.all { it.value } }

    override fun toString(): String {
        val falseString = falsePairs.map { it.first }.let {
            "[${it.joinToString(", ")}]"
        }

        val trueString = truePairs.map { it.first }.let {
            "[${it.joinToString(", ")}]"
        }

        return "CheckMachine(true=$trueString;false=$falseString)"
    }
}