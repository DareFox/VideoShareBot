package tools

/**
 * A class representing a CheckMachine with a set of checks, each associated with a string identifier.
 *
 * @constructor Creates a CheckMachine instance with the provided map of states.
 * @param states A map where each key represents a check identifier, and the corresponding value is a boolean
 * indicating the check's status.
 *
 * @property falsePairs
 * @property truePairs
 * @property isAllFalse
 * @property isAllTrue
 */
class CheckMachine(val states: Map<String, Boolean>) {
    /**
     * Constructor that initializes the CheckMachine with a map of states.
     *
     * @param states A map where keys are state names and values are boolean indicators.
     */
    constructor(states: List<Pair<String, Boolean>>) : this(states.toMap())
    /**
     * Constructor that initializes the CheckMachine with a vararg list of state pairs.
     *
     * @param states A vararg list of state pairs where each pair contains a state name and a boolean indicator.
     */
    constructor(vararg states: Pair<String, Boolean>) : this(states.toMap())

    /**
     * Lazily computed property that returns a list of pairs where the state is false.
     */
    val falsePairs by lazy { states.entries.filter { !it.value }.map { it.toPair() } }

    /**
     * Lazily computed property that returns a list of pairs where the state is true.
     */
    val truePairs by lazy { states.entries.filter { it.value }.map { it.toPair() } }

    /**
     * Lazily computed property that checks if all states are false.
     */
    val isAllFalse by lazy { states.all { !it.value } }

    /**
     * Lazily computed property that checks if all states are true.
     */
    val isAllTrue by lazy { states.all { it.value } }

    override fun toString(): String {
        val falseString = falsePairs.joinToString(", ", "[", "]") { it.first }
        val trueString = truePairs.joinToString(", ", "[", "]") { it.first }

        return "CheckMachine(true=$trueString; false=$falseString)"
    }
}