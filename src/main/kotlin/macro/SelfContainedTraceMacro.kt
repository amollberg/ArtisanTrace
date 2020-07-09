import coordinates.Coordinate
import java.io.File

data class SelfContainedTraceMacro(val model: Model, var cellSize: Double) {
    fun generate(area: Poly, startPoint: Coordinate) {
        val viaFile = File("src/test/resources/Via3.svg")
        // Note: These will be moved when the trace is created
        val startVia = model.addSvg(viaFile, startPoint)
        val endVia = model.addSvg(viaFile, startPoint)

        val grid = ArrayPolyGrid(area, cellSize)
        val walker = SpiralWalker(grid, grid.gridPosition(startPoint))
        val path = walker.generate()

        if (path.positions.isNotEmpty())
            createTrace(path, grid, startVia, endVia)
    }

    private fun createTrace(
        path: Path,
        grid: ArrayPolyGrid,
        startVia: SvgComponent,
        endVia: SvgComponent
    ): Trace {

        startVia.move(
            startVia.interfaces.first(),
            path.positions.first().coord(grid.system)
        )
        endVia.move(
            endVia.interfaces.first(),
            path.positions.last().coord(grid.system)
        )
        val trace = trace(model.system) {
            terminals(Terminals(startVia.interfaces.first(), 0..0))
            path.positions.drop(1).dropLast(1).forEach { gridPosition ->
                val position =
                    gridPosition.coord(grid.system).relativeTo(model.system)
                val itf = Interface(position, 0.0, 10.0, 1)
                model.interfaces.add(itf)
                terminals(Terminals(itf, 0..0))
            }
            terminals(Terminals(endVia.interfaces.first(), 0..0))
        }
        model.traces.add(trace)
        return trace
    }
}
