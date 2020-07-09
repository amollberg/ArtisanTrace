import coordinates.Coordinate

class ConcaveAreaMacroTool(viewModel: ViewModel) : BaseTool(viewModel) {
    val areaSelector = MouseHoverConcaveAreaSelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        val area = areaSelector.getArea() ?: return

        println(area)
    }

    override fun draw(drawer: OrientedDrawer) {
        areaSelector.draw(drawer)
    }
}
