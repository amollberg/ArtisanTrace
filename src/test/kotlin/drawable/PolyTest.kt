import Poly.Companion.rect
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2
import org.openrndr.shape.Triangle

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
            it.points.map { it.xy() }
        }
        assertEquals(
            listOf(
                listOf(
                    Vector2(100.0, 60.0),
                    Vector2(80.0, 50.0),
                    Vector2(100.0, 40.0)
                )
            ),
            concaveAreas
        )
    }

    @Test
    fun testFirstCommonSegment() {
        val a = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(100.0, 30.0),
                Vector2(100.0, 80.0),
                Vector2(0.0, 90.0)
            ).map { system.coord(it) })
        val b = Poly(
            listOf(
                Vector2(200.0, 0.0),
                Vector2(100.0, 30.0),
                Vector2(100.0, 80.0),
                Vector2(200.0, 95.0)
            ).map { system.coord(it) })

        assertEquals(a.segmentPointers[1], Poly.firstCommonSegment(a, b))
    }

    @Test
    fun firstCommonSegmentRealExample() {
        val a = Poly(
            listOf(
                Vector2(-5.0, 0.0),
                Vector2(85.0, 0.0),
                Vector2(95.0, 10.0),
                Vector2(105.0, 10.0),
                Vector2(95.0, 0.0),
                Vector2(5.0, 0.0)
            ).map { system.coord(it) })
        val b = Poly(
            listOf(
                Vector2(95.0, 10.0),
                Vector2(75.0, 30.0),
                Vector2(75.0, 50.0),
                Vector2(85.0, 50.0),
                Vector2(85.0, 30.0),
                Vector2(105.0, 10.0)
            ).map { system.coord(it) })

        assertNotEquals(null, Poly.firstCommonSegment(a, b.reversed))
    }

    @Test
    fun testJoin() {
        val a = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(100.0, 30.0),
                Vector2(100.0, 80.0),
                Vector2(0.0, 90.0)
            ).map { system.coord(it) })
        val b = Poly(
            listOf(
                Vector2(200.0, 95.0),
                Vector2(100.0, 80.0),
                Vector2(100.0, 30.0),
                Vector2(200.0, 0.0)
            ).map { system.coord(it) })

        assertEquals(listOf(
            Vector2(0.0, 90.0),
            Vector2(0.0, 0.0),
            Vector2(100.0, 30.0),
            Vector2(200.0, 0.0),
            Vector2(200.0, 95.0),
            Vector2(100.0, 80.0)
        ), Poly.join(a, b)!!.points.map { it.xyIn(system) })
    }

    @Test
    fun testFuse() {
        val a = Poly(
            listOf(
                Vector2(40.0, 0.0),
                Vector2(30.0, 10.0),
                Vector2(35.0, 20.0)
            ).map { system.coord(it) })
        val b = Poly(
            listOf(
                Vector2(100.0, 0.0),
                Vector2(110.0, 10.0),
                Vector2(105.0, 20.0)
            ).map { system.coord(it) })

        assertEquals(
            listOf(
                Vector2(40.0, 0.0),
                Vector2(30.0, 10.0),
                Vector2(35.0, 20.0),
                Vector2(105.0, 20.0),
                Vector2(110.0, 10.0),
                Vector2(100.0, 0.0)
            ), Poly.fuse(a, b).points.map { it.xyIn(system) })
    }

    @Test
    fun testTriangleArea() {
        val halfBase = 10.0
        val height = 17.0
        val tri = Triangle(
            Vector2(100.0, 200.0),
            Vector2(100.0 + halfBase, 200.0 + height),
            Vector2(100.0 - halfBase, 200.0 + height)
        )
        assertEquals(halfBase * height, area(tri))
    }

    @Test
    fun segmentOnConvexHull() {
        val a = Poly(
            listOf(
                Vector2(0.0, 0.0),
                Vector2(100.0, 0.0),
                Vector2(100.0, 100.0),
                Vector2(50.0, 80.0),
                Vector2(0.0, 100.0)
            ).map { system.coord(it) })
        assertEquals(
            setOf(
                a.segmentPointers[0],
                a.segmentPointers[1],
                a.segmentPointers[4]
            ),
            a.segmentsOnConvexHull
        )
    }
}
