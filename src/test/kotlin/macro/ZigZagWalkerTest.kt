import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZigZagWalkerTest {
    @Test
    fun fillsTrapezoid() {
        val walker = ZigZagWalker(
            ArrayLambdaGrid { (x, y) ->
                (2 - y..5 + y).contains(x) &&
                        (0..2).contains(y)
            },
            GridPosition(2, 0),
            Direction(0),
            TurnDirection.RIGHT
        )
        assertEquals(
            Path(
                mutableListOf(
                    GridPosition(2, 0),
                    GridPosition(5, 0),
                    GridPosition(5, 1),
                    GridPosition(1, 1),
                    GridPosition(1, 2),
                    GridPosition(7, 2)
                )
            ), walker.generate()
        )
    }

    @Test
    fun fillsNarrowingTrapezoid() {
        val walker = ZigZagWalker(
            ArrayLambdaGrid { (x, y) ->
                (y..6 - y).contains(x) &&
                        (0..2).contains(y)
            },
            GridPosition(0, 0),
            Direction(0),
            TurnDirection.RIGHT
        )
        assertEquals(
            Path(
                mutableListOf(
                    GridPosition(0, 0),
                    GridPosition(5, 0),
                    GridPosition(5, 1),
                    GridPosition(2, 1),
                    GridPosition(2, 2),
                    GridPosition(4, 2)
                )
            ), walker.generate()
        )
    }
}
