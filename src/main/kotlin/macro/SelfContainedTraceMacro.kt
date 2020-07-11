import TurnDirection.LEFT
import java.io.File

data class SelfContainedTraceMacro(
    val model: Model,
    var cellSize: Double,
    var startDirection: Direction
) {
    fun generate(area: Poly, startPoint: Coordinate): ModelAdditions {
        val previewModel = ModelAdditions(model)
        val viaFile = File("src/test/resources/Via3.svg")
        // Note: These will be moved when the trace is created
        val startVia = previewModel.addSvg(viaFile, startPoint)
        val endVia = previewModel.addSvg(viaFile, startPoint)

        val grid = ArrayPolyGrid(area, cellSize)
        val walker = SpiralWalker(
            grid,
            grid.position(startPoint),
            startDirection,
            LEFT
        )
        val path = walker.generate()

        if (path.positions.isNotEmpty())
            createTrace(previewModel, path, startVia, endVia)
        return previewModel
    }

    private fun createTrace(
        previewModel: ModelAdditions,
        path: Path,
        startVia: SvgComponent,
        endVia: SvgComponent
    ): Trace {
        startVia.move(
            startVia.interfaces.first(),
            path.positions.first().coordinate
        )
        endVia.move(
            endVia.interfaces.first(),
            path.positions.last().coordinate
        )
        val trace = trace(previewModel.system) {
            terminals(Terminals(startVia.interfaces.first(), 0..0))
            path.positions.drop(1).dropLast(1).forEach { gridPosition ->
                val itf = Interface(gridPosition.coordinate, 0.0, 10.0, 1)
                previewModel.addInterface(itf)
                terminals(Terminals(itf, 0..0))
            }
            terminals(Terminals(endVia.interfaces.first(), 0..0))
        }
        previewModel.addTrace(trace)
        return trace
    }
}
