import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceTest {

    @Test
    fun snapTo45() {
        assertEquals(
            Vector2(0.0, 100.0),
            snapTo45(Vector2.ZERO, Vector2(3.0, 100.0)))
    }

    @Test
    fun invertMatrix22() {
        var a = Matrix22(1.0, 3.14,
                         -5.3, 0.0)
        assertEquals(Matrix22.IDENTITY, a.times(a.invert()))
    }

    @Test
    fun arg() {
        assertEquals(0.0, arg(Vector2(1.0, 0.0)))
        assertEquals(45.0, arg(Vector2(1.0, 1.0)))
        assertEquals(-135.0, arg(Vector2(-1.0, -1.0)))
    }

    private fun assertEquals(a: Vector2, b: Vector2) {
        assertEquals(a.x, b.x)
        assertEquals(a.y, b.y)
    }
}
