import coordinates.Coordinate
import java.io.File

data class SelfContainedTraceMacro(val model: Model) {
    var lastTerminals: Terminals? = null

    fun generate(area: Poly, startPoint: Coordinate) {
        val viaFile = File("src/test/resources/Via2.svg")
        model.addSvg(viaFile, area.system!!.originCoord)

        val grid = ArrayPolyGrid(area, 10.0)
        val walker = SpiralWalker(grid, grid.gridPosition(startPoint))
        val path = walker.generate()

        createTrace(path, grid)
    }

    private fun createTrace(path: Path, grid: ArrayPolyGrid) {
        lastTerminals = null
        val trace = Trace(model.system)

        path.positions.forEach { gridPosition ->
            val position = gridPosition.coord(grid.system)
            addCorner(position.relativeTo(model.system), trace)
        }
        model.traces.add(trace)
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
