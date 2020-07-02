import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListUtilsTest {
    @Test
    fun testRotateFront() {
        val elements = listOf(0, 1, 2, 3, 4)
        assertEquals(listOf(1, 2, 3, 4, 0), rotateFront(elements))
    }

    @Test
    fun testCrossProduct() {
        val a = listOf(10, 20, 30)
        val b = listOf(0, 1, 2)
        assertEquals(
            listOf(
                Pair(10, 0),
                Pair(10, 1),
                Pair(10, 2),
                Pair(20, 0),
                Pair(20, 1),
                Pair(20, 2),
                Pair(30, 0),
                Pair(30, 1),
                Pair(30, 2)
            ),
            crossProduct(a, b)
        )
    }
}
