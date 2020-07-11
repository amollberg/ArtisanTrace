import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZigZagWalkerTest {
    @Test
    fun fillsTrapezoid() {
        val grid = ArrayLambdaGrid { (x, y) ->
            (2 - y..5 + y).contains(x) &&
                    (0..2).contains(y)
        }
        val walker = ZigZagWalker(
            grid,
            grid.position(2, 0),
            Direction(0),
            TurnDirection.RIGHT
        )
        assertEquals(
            Path(
                mutableListOf(
                    grid.position(2, 0),
                    grid.position(5, 0),
                    grid.position(5, 1),
                    grid.position(1, 1),
                    grid.position(1, 2),
                    grid.position(7, 2)
                )
            ), walker.generate()
        )
    }

    @Test
    fun fillsNarrowingTrapezoid() {
        val grid = ArrayLambdaGrid { (x, y) ->
            (y..6 - y).contains(x) &&
                    (0..2).contains(y)
        }
        val walker = ZigZagWalker(
            grid,
            grid.position(0, 0),
            Direction(0),
            TurnDirection.RIGHT
        )
        assertEquals(
            Path(
                mutableListOf(
                    grid.position(0, 0),
                    grid.position(5, 0),
                    grid.position(5, 1),
                    grid.position(2, 1),
                    grid.position(2, 2),
                    grid.position(4, 2)
                )
            ), walker.generate()
        )
    }
}
