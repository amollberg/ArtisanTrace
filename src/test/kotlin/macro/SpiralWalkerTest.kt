import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SpiralWalkerTest {
    @Test
    fun longThinAreaGeneratesALine() {
        val walker = SpiralWalker(
            ArrayLambdaGrid { (x, y) -> y == 0 && (0..3).contains(x) },
            GridPosition(0, 0)
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

    @Test
    fun doubleWideLongAreaGeneratesReturnHoop() {
        val walker = SpiralWalker(
            ArrayLambdaGrid { (x, y) ->
                (0..1).contains(y) && (0..3).contains(x)
            },
            GridPosition(0, 0)
        )
        assertEquals(
            Path(
                mutableListOf(
                    GridPosition(0, 0),
                    GridPosition(3, 0),
                    GridPosition(3, 1),
                    GridPosition(0, 1)
                )
            ), walker.generate()
        )
    }

    @Test
    fun tripleWideLongAreaGeneratesSpiral() {
        val walker = SpiralWalker(
            ArrayLambdaGrid { (x, y) ->
                (0..2).contains(y) && (0..3).contains(x)
            },
            GridPosition(0, 0)
        )
        assertEquals(
            Path(
                mutableListOf(
                    GridPosition(0, 0),
                    GridPosition(3, 0),
                    GridPosition(3, 2),
                    GridPosition(0, 2),
                    GridPosition(0, 1),
                    GridPosition(2, 1)
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
