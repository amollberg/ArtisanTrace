import coordinates.System
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SpiralWalkerTest {
    @Test
    fun longThinAreaGeneratesALine() {
        val grid = ArrayLambdaGrid { (x, y) -> y == 0 && (0..3).contains(x) }
        val walker = SpiralWalker(
            grid,
            grid.position(Vector2.ZERO),
            Direction(0),
            TurnDirection.LEFT
        )
        assertEquals(
            Path(
                mutableListOf(
                    grid.position(0, 0),
                    grid.position(3, 0)
                )
            ), walker.generate()
        )
    }
}

class ArrayLambdaGrid(val bounds: (position: GridPosition) -> Boolean) :
    ArrayGrid(Vector2(10.0, 10.0)) {
    override val system: System = System.root()

    override fun isInBounds(position: GridPosition) =
        bounds(position)
}
