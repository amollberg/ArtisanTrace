import TestUtils.Companion.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

internal class TraceSegmentTest : WithImplicitView() {
    @Test
    fun traceSegment1() {
        var s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 10.0),
            Angle.OBTUSE,
            false,
            view.root
        )
        assertEquals(at(10.0, 0.0), s.getKnee())
    }

    @Test
    fun traceSegment2() {
        var s = TraceSegment(
            terminalsAt(10.0, 5.0),
            terminalsAt(110.0, 205.0),
            Angle.OBTUSE,
            false,
            view.root
        )
        assertEquals(at(10.0, 105.0), s.getKnee())
    }

    @Test
    fun traceSegment1Reverse() {
        var s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 10.0),
            Angle.OBTUSE,
            true,
            view.root
        )
        assertEquals(at(10.0, 10.0), s.getKnee())
    }

    @Test
    fun traceSegment2Reverse() {
        var s = TraceSegment(
            terminalsAt(10.0, 5.0),
            terminalsAt(110.0, 205.0),
            Angle.OBTUSE,
            true,
            view.root
        )
        assertEquals(at(110.0, 105.0), s.getKnee())
    }

    @Test
    fun bounds1() {
        val s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 100.0),
            Angle.OBTUSE,
            false,
            view.root
        )
        val actual = s.bounds.points.map { it.xyIn(view.root) }
        assertEquals(
            listOf(
                Vector2(-5.0, 0.0),
                Vector2(-5.0, 80.0),
                Vector2(15.0, 100.0),
                Vector2(25.0, 100.0),
                Vector2(5.0, 80.0),
                Vector2(5.0, 0.0)
            ), actual
        )
    }

    @Test
    fun bounds1Reverse() {
        val s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 100.0),
            Angle.OBTUSE,
            true,
            view.root
        )
        val actual = s.bounds.points.map { it.xyIn(view.root) }
        assertEquals(
            listOf(
                Vector2(-5.0, 0.0),
                Vector2(15.0, 20.0),
                Vector2(15.0, 100.0),
                Vector2(25.0, 100.0),
                Vector2(25.0, 20.0),
                Vector2(5.0, 0.0)
            ), actual
        )
    }

    @Test
    fun bounds1Twisted() {
        val s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(20.0, 100.0, 180.0),
            Angle.OBTUSE,
            false,
            view.root
        )
        val actual = s.bounds.points.map { it.xyIn(view.root) }
        assertEquals(
            listOf(
                Vector2(-5.0, 0.0),
                Vector2(-5.0, 80.0),
                Vector2(15.0, 100.0),
                Vector2(25.0, 100.0),
                Vector2(5.0, 80.0),
                Vector2(5.0, 0.0)
            ), actual
        )
    }

    @Test
    fun bounds2Collapsed() {
        val s = TraceSegment(
            terminalsAt(0.0, 0.0),
            terminalsAt(100.0, 10.0),
            Angle.OBTUSE,
            false,
            view.root
        )
        val actual = s.bounds.points.map { it.xyIn(view.root) }
        assertEquals(
            listOf(
                Vector2(-5.0, 0.0),
                Vector2(95.0, 0.0),
                Vector2(105.0, 10.0),
                Vector2(95.0, 10.0),
                Vector2(85.0, 0.0),
                Vector2(5.0, 0.0)
            ), actual
        )
    }

    private fun terminalsAt(x: Double, y: Double, angle: Double = 0.0) =
        Terminals(
            Interface(at(x, y), angle, 10.0, 1),
            0..0
        )
}
