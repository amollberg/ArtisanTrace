import TurnDirection.LEFT
import coordinates.Coordinate
import coordinates.System
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
            grid.gridPosition(startPoint),
            startDirection,
            LEFT
        )
        val path = walker.generate()

        if (path.positions.isNotEmpty())
            createTrace(previewModel, path, grid, startVia, endVia)
        return previewModel
    }

    private fun createTrace(
        previewModel: ModelAdditions,
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
        val trace = trace(previewModel.system) {
            terminals(Terminals(startVia.interfaces.first(), 0..0))
            path.positions.drop(1).dropLast(1).forEach { gridPosition ->
                val position =
                    gridPosition.coord(grid.system)
                        .relativeTo(previewModel.system)
                val itf = Interface(position, 0.0, 10.0, 1)
                previewModel.addInterface(itf)
                terminals(Terminals(itf, 0..0))
            }
            terminals(Terminals(endVia.interfaces.first(), 0..0))
        }
        previewModel.addTrace(trace)
        return trace
    }
}

data class ModelAdditions(val model: Model) {
    val system: System get() = model.system

    private var interfaces: MutableList<Interface> = mutableListOf()
    private var traces: MutableList<Trace> = mutableListOf()
    private var svgComponents: MutableList<SvgComponent> = mutableListOf()

    fun addInterface(itf: Interface) {
        interfaces.add(itf)
    }

    fun addTrace(trace: Trace) {
        traces.add(trace)
    }

    fun addSvg(backingFile: File, coordinate: Coordinate): SvgComponent {
        val svgComponent = model.loadSvg(backingFile, coordinate)
        svgComponents.add(svgComponent)
        return svgComponent
    }

    fun commit() {
        model.interfaces.addAll(interfaces)
        model.traces.addAll(traces)
        svgComponents.forEach { model.addSvg(it) }

        interfaces.clear()
        traces.clear()
        svgComponents.clear()
    }

    fun draw(drawer: OrientedDrawer) {
        isolatedStyle(drawer.drawer, stroke = model.color) {
            traces.forEach { it.draw(drawer) }
            interfaces.forEach { it.draw(drawer) }
            svgComponents.forEach { it.draw(drawer) }
        }
    }
}
