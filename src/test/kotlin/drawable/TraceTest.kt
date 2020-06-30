import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceTest : WithImplicitView() {

    @Test
    fun snapTo45() {
        assertEquals(
            Vector2(0.0, 100.0),
            snapTo45(Vector2.ZERO, Vector2(3.0, 100.0))
        )
    }

    @Test
    fun boundsTest() {
        val terminals = listOf(
            terminalsAt(0.0, 0.0),
            terminalsAt(100.0, 10.0),
            terminalsAt(80.0, 50.0)
        )
        val s1 = TraceSegment(
            terminals[0],
            terminals[1],
            Angle.OBTUSE,
            false,
            view.root
        )
        val s2 = TraceSegment(
            terminals[1],
            terminals[2],
            Angle.OBTUSE,
            true,
            view.root
        )
        val t = Trace(view.root, listOf(s1, s2))
        assertEquals(t.bounds, Poly.join(s1.bounds, s2.bounds))
    }

    @Test
    fun arg() {
        assertEquals(0.0, arg(Vector2(1.0, 0.0)))
        assertEquals(45.0, arg(Vector2(1.0, 1.0)))
        assertEquals(-135.0, arg(Vector2(-1.0, -1.0)))
    }

    private fun terminalsAt(x: Double, y: Double, angle: Double = 0.0) =
        Terminals(
            Interface(at(x, y), angle, 10.0, 1),
            0..0
        )
}
