import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceSegmentTest {
    @Test
    fun traceSegment1() {
        var s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 10.0),
            Angle.OBTUSE
        )
        assertEquals(Vector2(10.0, 0.0), s.getKnee())
    }

    @Test
    fun traceSegment2() {
        var s = TraceSegment(
            terminalsAt(10.0, 5.0),
            terminalsAt(110.0, 205.0),
            Angle.OBTUSE
        )
        assertEquals(Vector2(10.0, 105.0), s.getKnee())
    }

    private fun terminalsAt(x: Double, y: Double) =
        Terminals(
            Interface(Vector2(x, y), 0.0, 1.0, 1),
            0..0
        )
}
