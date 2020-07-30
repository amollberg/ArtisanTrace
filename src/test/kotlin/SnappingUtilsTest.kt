import Poly.Companion.rect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SnappingUtilsTest : WithImplicitView() {
    @Test
    fun measuredAreaOfTraceIsNonzero() {
        assertEquals(
            10 * 100.0,
            measureOverlappingArea(trace.bounds, trace.bounds)
        )
    }

    @Test
    fun measuredAreaOfRectIsNonzero() {
        assertEquals(
            20 * 20.0,
            measureOverlappingArea(rect, rect)
        )
    }

    @Test
    fun nonTrivialOverlapTraceAndRectGivesNonzeroArea() {
        assertEquals(
            10 * 20.0,
            measureOverlappingArea(rect, trace.bounds)
        )
        assertEquals(
            (10 - 8) * 20.0,
            measureOverlappingArea(
                rect.moved(view.root.length(Vector2(8.0, 30.0))),
                trace.bounds
            )
        )
    }

    private val rect
        get() = rect(view.root, 20, 20)

    private val trace
        get() = trace(view.root) {
            terminals(Interface(at(5, 0), 0.0, 10.0, 1).getTerminals())
            terminals(Interface(at(5, 100), 0.0, 10.0, 1).getTerminals())
        }
}
