import coordinates.Coordinate
import coordinates.System
import org.openrndr.math.Vector2
import kotlin.math.round

data class GridPosition(
    val x: Int,
    val y: Int,
    private val gridSystem: System
) {
    companion object {
        fun from(coordinate: Coordinate, gridSystem: System) =
            GridPosition(coordinate.xyIn(gridSystem), gridSystem)
    }

    constructor(xy: Vector2, gridSystem: System) :
            this(round(xy.x).toInt(), round(xy.y).toInt(), gridSystem)

    val coordinate: Coordinate
        get() = gridSystem.coord(Vector2(x.toDouble(), y.toDouble()))

    fun neighbor(direction: Direction): GridPosition {
        val xOffset = when (direction.angle45) {
            0 -> 1
            1 -> 1
            2 -> 0
            3 -> -1
            4 -> -1
            5 -> -1
            6 -> 0
            7 -> 1
            else -> throw RuntimeException("Invalid angle")
        }
        val yOffset = when (direction.angle45) {
            0 -> 0
            1 -> -1
            2 -> -1
            3 -> -1
            4 -> 0
            5 -> 1
            6 -> 1
            7 -> 1
            else -> throw RuntimeException("Invalid angle")
        }
        return GridPosition(x + xOffset, y + yOffset, gridSystem)
    }
}

data class Path(val positions: MutableList<GridPosition>)

