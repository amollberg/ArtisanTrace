import coordinates.System
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class GridTest {

    @Test
    fun arrayPolyGridRectangle() {
        val system = System.root()
        val poly = Poly.rect(system, 20, 10)
        val grid = ArrayPolyGrid(poly, 10.0)

        assertFalse(grid.isInBounds(grid.position(-1, 0)))
        assertTrue(grid.isInBounds(grid.position(0, 0)))
        assertTrue(grid.isInBounds(grid.position(1, 0)))
        assertTrue(grid.isInBounds(grid.position(2, 0)))
        assertFalse(grid.isInBounds(grid.position(3, 0)))

        assertFalse(grid.isInBounds(grid.position(-1, 1)))
        assertTrue(grid.isInBounds(grid.position(0, 1)))
        assertTrue(grid.isInBounds(grid.position(1, 1)))
        assertTrue(grid.isInBounds(grid.position(2, 1)))
        assertFalse(grid.isInBounds(grid.position(3, 1)))

        poly.points.forEach {
            // Make sure it is within bounds of the underlying array
            grid.visit(grid.position(it))
        }
    }

    @Test
    fun irregularQuad() {
        val system = System.root()
        val poly = Poly(listOf(
            Vector2(30.0, 20.0),
            Vector2(10.0, 90.0),
            Vector2(80.0, 70.0),
            Vector2(50.0, 0.0)
        ).map { system.coord(it) })
        val grid = ArrayPolyGrid(poly, 10.0)

        poly.points.forEach {
            // Make sure it is within bounds of the underlying array
            grid.visit(grid.position(it))
        }
    }
}
