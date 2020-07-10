import TurnDirection.LEFT
import TurnDirection.RIGHT

class ZigZagWalker(
    val grid: Grid,
    val startPosition: GridPosition,
    val startDirection: Direction,
    val startingTurnDirection: TurnDirection
) {
    fun generate(): Path {
        val path = Path(mutableListOf())

        if (!grid.isInBounds(startPosition)) return path
        grid.visit(startPosition)
        path.positions.add(startPosition)
        var position = startPosition
        var direction = startDirection
        fun canGo(relativeDirection: Int) =
            position.neighbor(relativeDirection + direction).let { neighbor ->
                grid.isInBounds(neighbor) && !grid.hasVisited(neighbor)
            }

        fun go(relativeDirection: Int) {
            if (!canGo(relativeDirection))
                throw IllegalArgumentException("Cannot go $relativeDirection")
            direction += relativeDirection
            position = position.neighbor(direction)
            grid.visit(position)
            if (relativeDirection == 0 && path.positions.size > 1) {
                path.positions.removeAt(path.positions.size - 1)
            }
            path.positions.add(position)
        }

        var turnDirection = startingTurnDirection
        fun turn() =
            when (turnDirection) {
                LEFT -> 2
                RIGHT -> -2
            }
        while (true) {
            if (!canGo(0)) {
                // Turn around
                if (canGo(turn())) {
                    go(turn())
                    if (canGo(turn())) {
                        go(turn())
                    } else {
                        break
                    }
                } else {
                    break
                }
                turnDirection = when (turnDirection) {
                    LEFT -> RIGHT
                    RIGHT -> LEFT
                }
            } else {
                go(0)
            }
        }
        return path
    }
}
