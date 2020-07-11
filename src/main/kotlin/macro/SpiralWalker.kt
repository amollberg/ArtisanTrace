data class SpiralWalker(
    val grid: Grid,
    val startPosition: GridPosition,
    val turnDirection: TurnDirection
) {
    fun generate(): Path {
        val path = Path(mutableListOf())

        if (!grid.isInBounds(startPosition)) return path
        grid.visit(startPosition)
        path.positions.add(startPosition)
        var position = startPosition

        var direction = Direction(0)
        while (true) {
            val availableDirection =
                when (turnDirection) {
                    TurnDirection.LEFT -> listOf(2, 0)
                    TurnDirection.RIGHT -> listOf(-2, 0)
                }.map {
                    it + direction
                }.filter {
                    position.neighbor(it).let { neighbor ->
                        grid.isInBounds(neighbor) && !grid.hasVisited(neighbor)
                    }
                }.firstOrNull() ?: break

            val availableNeighbor = position.neighbor(availableDirection)
            grid.visit(availableNeighbor)
            position = availableNeighbor
            if (availableDirection == direction && path.positions.size > 1) {
                path.positions.removeAt(path.positions.size - 1)
            }
            path.positions.add(availableNeighbor)

            direction = availableDirection
        }
        return path
    }
}

enum class TurnDirection {
    LEFT, RIGHT
}
