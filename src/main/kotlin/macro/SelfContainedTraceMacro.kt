import coordinates.Coordinate
import java.io.File

data class SelfContainedTraceMacro(val model: Model, var cellSize: Double) {
    var lastTerminals: Terminals? = null

    fun generate(area: Poly, startPoint: Coordinate) {
        val viaFile = File("src/test/resources/Via2.svg")
        val startVia = model.addSvg(viaFile, startPoint)
        val traceStartPoint = startVia.interfaces.first().center

        val grid = ArrayPolyGrid(area, cellSize)
        val walker = SpiralWalker(grid, grid.gridPosition(traceStartPoint))
        val path = walker.generate()

        val trace = createTrace(path, grid)

        val endPoint = grid.coordinate(path.positions.last())
        val endVia = model.addSvg(viaFile, endPoint)
        trace.append(Terminals(endVia.interfaces.first(), 0..0))
    }

    private fun createTrace(path: Path, grid: ArrayPolyGrid): Trace {
        lastTerminals = null
        val trace = Trace(model.system)

        path.positions.forEach { gridPosition ->
            val position = gridPosition.coord(grid.system)
            addCorner(position.relativeTo(model.system), trace)
        }
        model.traces.add(trace)
        return trace
    }

    private fun addCorner(coordinate: Coordinate, trace: Trace) {
        val itf = Interface(coordinate, 0.0, 10.0, 1)
        model.interfaces.add(itf)

        val terminals = Terminals(itf, 0..0)
        lastTerminals?.ifPresent {
            val segment =
                TraceSegment(
                    it,
                    terminals,
                    Angle.OBTUSE,
                    false
                )
            trace.add(segment)
        }
        lastTerminals = terminals
    }
}
