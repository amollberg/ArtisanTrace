import coordinates.Coordinate

class PolyDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val points = mutableListOf<Coordinate>()

    override fun mouseClicked(position: Coordinate) {
        points.add(viewModel.mousePoint)
    }

    override fun draw(drawer: OrientedDrawer) {
        Poly(points + viewModel.mousePoint).draw(drawer)
    }

    override fun exit() {
        if (!Poly(points).isTrivial) {
            viewModel.model.polys.add(Poly(points))
        }
    }
}
