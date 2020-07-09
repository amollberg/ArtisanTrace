data class SpiralWalker(val grid: Grid, val startPosition: GridPosition) {
    fun generate(): Path {
        val path = Path(mutableListOf())

        if (!grid.isInBounds(startPosition)) return path
        grid.visit(startPosition)
        path.positions.add(startPosition)
        var position = startPosition

        var direction = Direction.of(0)
        while (true) {
            val availableDirection =
                listOf(0, 1, -1, 2, -2, 3, -3, -4).map {
                    Direction.of(it + direction.angle45)
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
