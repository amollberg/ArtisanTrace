import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceSegmentTest {
    @Test
    fun traceSegment1() {
        var s = TraceSegment(
            Vector2(0.0, 0.0),
            Vector2(20.0, 10.0),
            Angle.OBTUSE
        )
        assertEquals(Vector2(10.0, 0.0), s.getKnee())
    }

    @Test
    fun traceSegment2() {
        var s = TraceSegment(
            Vector2(10.0, 5.0),
            Vector2(110.0, 205.0),
            Angle.OBTUSE
        )
        assertEquals(Vector2(10.0, 105.0), s.getKnee())
    }
}
