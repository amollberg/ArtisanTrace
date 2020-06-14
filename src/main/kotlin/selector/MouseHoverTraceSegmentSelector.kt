class MouseHoverTraceSegmentSelector(private val viewModel: ViewModel) {

    fun getSegment(): TraceSegment? =
        viewModel.model.traces.flatMap { trace ->
            trace.segments
        }.minBy { segment ->
            distanceToMouse(segment) ?: Double.POSITIVE_INFINITY
        }

    private fun distanceToMouse(traceSegment: TraceSegment) =
        traceSegment.lineSegments(viewModel.root).map {
            it.distance(viewModel.mousePoint.xyIn(viewModel.root))
        }.min()

    fun draw(drawer: OrientedDrawer) {
        val segment = getSegment() ?: return
        isolatedStyle(
            drawer.drawer,
            strokeWeight = drawer.drawer.strokeWeight * 3
        ) {
            it.lineSegments(segment.lineSegments(drawer.system))
        }
    }
}
