import coordinates.Coordinate
import coordinates.System
import org.openrndr.math.Vector2

interface Grid {
    fun hasVisited(position: GridPosition): Boolean

    fun isInBounds(position: GridPosition): Boolean

    fun visit(position: GridPosition)
}

abstract class ArrayGrid(dimensions: Vector2) : Grid {
    private val grid: Array<Array<Boolean>> =
        create2dBooleanArray(dimensions)

    override fun hasVisited(position: GridPosition) =
        grid.getOrNull(position.y)?.getOrNull(position.x) ?: false

    override fun visit(position: GridPosition) {
        if (grid[position.y][position.x])
            throw RuntimeException("$position already visited")
        grid[position.y][position.x] = true
    }
}

fun gridSystem(poly: Poly, cellSize: Double): System {
    val minimumX = poly.points.map { it.xyIn(poly.system!!).x }.min() ?: 0.0
    val minimumY = poly.points.map { it.xyIn(poly.system!!).y }.min() ?: 0.0
    return poly.system!!.createSystem(
        origin = Vector2(minimumX, minimumY),
        axes = Matrix22.IDENTITY * cellSize
    )
}

fun cellBounds(poly: Poly, cellSize: Double) =
    poly.contour(
        gridSystem(poly, cellSize)
    ).bounds.dimensions

class ArrayPolyGrid(
    val poly: Poly,
    private val cellSize: Double
) : ArrayGrid(cellBounds(poly, cellSize)) {
    val system = gridSystem(poly, cellSize)

    override fun isInBounds(position: GridPosition) =
        poly.contains(position.coord(system))

    fun gridPosition(coordinate: Coordinate) =
        GridPosition.from(coordinate.xyIn(system))

    fun coordinate(gridPosition: GridPosition) =
        gridPosition.coord(system)
}

fun create2dBooleanArray(dimensions: Vector2) =
    Array(dimensions.y.toInt() + 1) {
        Array(dimensions.x.toInt() + 1) {
            false
        }
    }

