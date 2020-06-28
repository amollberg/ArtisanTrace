import Poly.Companion.rect
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class PolyTest {
    companion object {
        val system = root()
    }

    @Test
    fun testPointsAfter() {
        val poly = rect(system, 20, 30)
        val points = listOf(
            Vector2(0.0, 0.0),
            Vector2(20.0, 0.0),
            Vector2(20.0, 30.0),
            Vector2(0.0, 30.0)
        ).map { system.coord(it) }
        assertEquals(points, poly.points)

        assertEquals(
            listOf(points[1], points[2], points[3]),
            poly.pointsAfter(points[0])
        )
    }

    @Test
    fun testConcaveAreas() {
        val poly = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(100.0, 0.0),
                // Inward spike
                Vector2(100.0, 40.0),
                Vector2(80.0, 50.0),
                Vector2(100.0, 60.0),

                Vector2(100.0, 100.0),
                Vector2(0.0, 100.0)
            ).map { system.coord(it) }
        )
        val concaveAreas = poly.concaveAreas.map {
            it.contours.map {
                it.segments.flatMap {
                    it.control.asList() + it.start
                }.map { Pair(it.x, it.y) }
            }
        }
        assertEquals(
            listOf(
                listOf(
                    listOf(
                        Pair(100.0, 60.0),
                        Pair(80.0, 50.0),
                        Pair(100.0, 40.0)
                    )
                )
            ),
            concaveAreas
        )
    }
}
