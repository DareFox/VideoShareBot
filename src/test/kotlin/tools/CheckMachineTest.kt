package tools

import tools.CheckMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckMachineTest {
    @Test
    fun `Test constructor with map`() {
        val expectedStates = mapOf("A" to true, "B" to false, "C" to true)
        assertEquals(expectedStates, CheckMachine(expectedStates).states)
    }

    @Test
    fun `Test constructor with pairs`() {
        val pairs = listOf("A" to true, "B" to false, "C" to true)
        val expectedStates = mapOf("A" to true, "B" to false, "C" to true)
        assertEquals(expectedStates, CheckMachine(pairs).states)
    }

    @Test
    fun `Test constructor with varargs`() {
        val expectedStates = mapOf("A" to true, "B" to false, "C" to true)
        assertEquals(expectedStates, CheckMachine("A" to {true}, "B" to {false}, "C" to {true}).states)
    }

    @Test
    fun `Test false pairs`() {
        val states = mapOf("A" to true, "B" to false, "C" to true)
        val expectedFalsePairs = listOf("B" to false)
        assertEquals(expectedFalsePairs, CheckMachine(states).falsePairs)
    }

    @Test
    fun `Test true pairs`() {
        val states = mapOf("A" to true, "B" to false, "C" to true)
        val expectedTruePairs = listOf("A" to true, "C" to true)
        assertEquals(expectedTruePairs, CheckMachine(states).truePairs)
    }

    @Test
    fun `Test isAllFalse with all false states`() {
        val states = mapOf("A" to false, "B" to false, "C" to false)
        assertTrue(CheckMachine(states).isAllFalse)
    }

    @Test
    fun `Test isAllFalse with mixed states`() {
        val states = mapOf("A" to true, "B" to false, "C" to true)
        assertFalse(CheckMachine(states).isAllFalse)
    }

    @Test
    fun `Test isAllTrue with all true states`() {
        val states = mapOf("A" to true, "B" to true, "C" to true)
        assertTrue(CheckMachine(states).isAllTrue)
    }

    @Test
    fun `Test isAllTrue with mixed states`() {
        val states = mapOf("A" to true, "B" to false, "C" to true)
        assertFalse(CheckMachine(states).isAllTrue)
    }

    @Test
    fun `Test toString`() {
        val states = mapOf("A" to true, "B" to false, "C" to true)
        val expectedString = "CheckMachine(true=[A, C]; false=[B])"
        assertEquals(expectedString, CheckMachine(states).toString())
    }
}
