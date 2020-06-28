import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListUtilsTest {
    @Test
    fun testRotateFront() {
        val elements = listOf(0, 1, 2, 3, 4)
        assertEquals(listOf(1, 2, 3, 4, 0), rotateFront(elements))
    }
}
