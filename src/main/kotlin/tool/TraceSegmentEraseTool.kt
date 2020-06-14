import coordinates.Coordinate

class TraceSegmentEraseTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val segmentSelector = MouseHoverTraceSegmentSelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        val segment = segmentSelector.getSegment() ?: return
        viewModel.model.eraseSegment(segment)
    }

    override fun draw(drawer: OrientedDrawer) {
        segmentSelector.draw(drawer)
    }
}
