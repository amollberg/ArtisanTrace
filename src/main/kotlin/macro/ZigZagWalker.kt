import TurnDirection.LEFT
import TurnDirection.RIGHT

class ZigZagWalker(
    val grid: Grid,
    val startPosition: GridPosition,
    val startingTurnDirection: TurnDirection
) {
    fun generate(): Path {
        val path = Path(mutableListOf())

        if (!grid.isInBounds(startPosition)) return path
        grid.visit(startPosition)
        path.positions.add(startPosition)
        var position = startPosition
        var direction = Direction(0)
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

        fun turnDiagonal() = turn() / 2
        fun switchTurning() {
            turnDirection = when (turnDirection) {
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
        }
        while (true) {
            if (canGo(turn())) {
                if (canGo(turnDiagonal())) {
                    if (canGo(0)) {
                        go(0)
                    } else {
                        // Turn around
                        go(turn())
                        if (!canGo(turn())) {
                            break
                        }
                        go(turn())
                        switchTurning()
                    }
                } else {
                    // Turn around
                    go(turn())
                    if (!canGo(turn())) {
                        break
                    }
                    go(turn())
                    switchTurning()
                }
            } else {
                if (canGo(0)) {
                    go(0)
                } else {
                    break
                }
            }
        }
        return path
    }
}
