import coordinates.Coordinate

class ConcaveAreaMacroTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val areaSelector = MouseHoverPolySelector(viewModel)
    val macro = SelfContainedTraceMacro(viewModel.model, 7.0, Direction(0))

    override fun mouseClicked(position: Coordinate) {
        val area = areaSelector.getPoly() ?: return
        val previewModel = macro.generate(area, viewModel.mousePoint)
        previewModel.commit()
    }

    override fun draw(drawer: OrientedDrawer) {
        areaSelector.draw(drawer)
        val area = areaSelector.getPoly() ?: return
        val previewModel = macro.generate(area, viewModel.mousePoint)
        previewModel.draw(drawer)
    }
}
