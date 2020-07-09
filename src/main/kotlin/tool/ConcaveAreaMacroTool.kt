import coordinates.Coordinate

class ConcaveAreaMacroTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val areaSelector = MouseHoverPolySelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        val area = areaSelector.getPoly() ?: return
        val macro = SelfContainedTraceMacro(viewModel.model, 7.0)
        macro.generate(area, viewModel.mousePoint)
    }

    override fun draw(drawer: OrientedDrawer) {
        areaSelector.draw(drawer)
    }
}
