import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceTest {

    @Test
    fun snapTo45() {
        assertEquals(
            Vector2(0.0, 100.0),
            snapTo45(Vector2.ZERO, Vector2(3.0, 100.0))
        )
    }

    @Test
    fun arg() {
        assertEquals(0.0, arg(Vector2(1.0, 0.0)))
        assertEquals(45.0, arg(Vector2(1.0, 1.0)))
        assertEquals(-135.0, arg(Vector2(-1.0, -1.0)))
    }
}
