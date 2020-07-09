import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SpiralWalkerTest {
    @Test
    fun longThinAreaGeneratesALine() {
        val walker = SpiralWalker(
            ArrayLambdaGrid { (x, y) -> y == 0 && (0..3).contains(x) },
            GridPosition(0, 0),
            Direction(0),
            TurnDirection.LEFT
        )
        assertEquals(
            Path(
                mutableListOf(
                    GridPosition(0, 0),
                    GridPosition(3, 0)
                )
            ), walker.generate()
        )
    }
}

class ArrayLambdaGrid(val bounds: (position: GridPosition) -> Boolean) :
    ArrayGrid(Vector2(10.0, 10.0)) {

    override fun isInBounds(position: GridPosition) =
        bounds(position)
}
