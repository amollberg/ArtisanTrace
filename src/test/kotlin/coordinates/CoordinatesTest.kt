package coordinates

import Matrix22
import Matrix22.Companion.rotation
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector2
import kotlin.math.PI

class CoordinatesTest {
    val root = System.root()
    val v = root.createSystem(Vector2(50.0, 20.0))
    val w = root.createSystem(
        Vector2(50.0, 20.0), axes = Matrix22(1, 0, 0, -1)
    )

    @Test
    fun mirroredY() {
        val c = v.coord(Vector2(10.0, 30.0))
        assertEquals(
            Vector2(10.0 + 50.0, 20.0 + 30.0),
            c.relativeTo(root).xy()
        )
        assertEquals(Vector2(10.0, 30.0), c.relativeTo(v).xy())
        assertEquals(Vector2(10.0, -30.0), c.relativeTo(w).xy())
    }

    @Test
    fun positionPlusLength() {
        val m = w.coord(Vector2(40.0, 5.0))
        var l = root.length(Vector2(10.0, 30.0))
        assertEquals(
            Vector2(50.0 + 40.0 + 10.0, 20.0 - 5.0 + 30.0),
            m.plus(l).relativeTo(root).xy()
        )
    }

    @Test
    fun changingAxes() {
        val xy = Vector2(10.0, 20.0)
        var changing = root.createSystem(xy)
        val c = changing.coord(Vector2(1.0, 2.0))
        assertEquals(Vector2(11.0, 22.0), c.relativeTo(root).xy())
        changing.originCoord = root.coord(Vector2(30.0, 40.0))
        assertEquals(Vector2(31.0, 42.0), c.relativeTo(root).xy())
    }

    @Test
    fun inversedMatrix33() {
        val m = Matrix33(
            2.0, 4.0, 5.0,
            -3.0, 3.14, 8.2,
            47.0, 11.0, -17.0
        )
        assertEquals(Matrix33.IDENTITY, m * inversed(m), delta = 1e-14)
    }

    @Test
    fun setAbsolute() {
        val root = System.root()
        val a = root.createSystem(Vector2(-2.3, 4.5))
        val b = root.createSystem(Vector2(3.14, 47.11))
        b.axes *= 1.4
        b.axes *= rotation(30 * PI / 180)

        a.setAbsoluteFrom(b)
        assertEquals(a.absoluteTransform(), b.absoluteTransform())
    }

    private fun assertEquals(a: Matrix33, b: Matrix33, delta: Double) {
        (0..2).forEach { c ->
            (0..2).forEach { r ->
                assertEquals(a[c][r], b[c][r], delta)
            }
        }
    }
}
