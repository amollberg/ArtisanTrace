import java.io.File

data class SelfContainedTraceMacro(
    val model: Model
) {
    fun generate(walker: Walker): ModelAdditions {
        val previewModel = ModelAdditions(model)
        val viaFile = File("src/test/resources/Via3.svg")
        // Note: These will be moved when the trace is created
        val startVia = previewModel.addSvg(viaFile, model.system.originCoord)
        val endVia = previewModel.addSvg(viaFile, model.system.originCoord)

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
            path.positions.first().coordinate.relativeTo(model.system)
        )
        endVia.move(
            endVia.interfaces.first(),
            path.positions.last().coordinate.relativeTo(model.system)
        )
        val trace = trace(previewModel.system) {
            terminals(Terminals(startVia.interfaces.first(), 0..0))
            path.positions.drop(1).dropLast(1).forEach { gridPosition ->
                val itf = Interface(
                    gridPosition.coordinate.relativeTo(model.system),
                    0.0,
                    0.01,
                    1
                )
                previewModel.addInterface(itf)
                terminals(Terminals(itf, 0..0))
            }
            terminals(Terminals(endVia.interfaces.first(), 0..0))
        }
        previewModel.addTrace(trace)
        return trace
    }
}
