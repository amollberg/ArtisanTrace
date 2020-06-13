import coordinates.Coordinate

/** Tool to chop up a trace segment and insert an interface in-between */
class InterfaceInsertTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {

    override fun mouseClicked(position: Coordinate) {
        val (trace, seg) = getNearestSegment(position) ?: return
        val newFirstSegment = TraceSegment(
            seg.getStart(),
            itf.getTerminals(),
            seg.angle,
            seg.reverseKnee,
            seg.system
        )
        val newSecondSegment = TraceSegment(
            itf.getTerminals(),
            seg.getEnd(),
            seg.angle,
            seg.reverseKnee,
            seg.system
        )
        // Replace the segment with the two new ones that go via the mouse
        trace.replace(seg, listOf(newFirstSegment, newSecondSegment))

        viewModel.model.interfaces.add(itf)
        itf = itf.clone()
    }

    override fun draw(drawer: OrientedDrawer) {
        val position = viewModel.mousePoint
        val (_, seg) = getNearestSegment(position) ?: return
        itf.center = viewModel.mousePoint
        val newFirstSegment = TraceSegment(
            seg.getStart(),
            itf.getTerminals(),
            seg.angle,
            seg.reverseKnee,
            seg.system
        )
        val newSecondSegment = TraceSegment(
            itf.getTerminals(),
            seg.getEnd(),
            seg.angle,
            seg.reverseKnee,
            seg.system
        )

        newFirstSegment.draw(drawer)
        newSecondSegment.draw(drawer)
        itf.draw(drawer)
    }

    private fun getNearestSegment(position: Coordinate):
            Pair<Trace, TraceSegment>? {
        return viewModel.model.traces.flatMap { trace ->
            trace.segments.map { segment ->
                Triple(trace, segment, segment.getKnee())
            }
        }.minBy { (_, _, kneePosition) ->
            (kneePosition - position).xyIn(position.system).length
        }?.let { (trace, segment, _) -> Pair(trace, segment) }
    }
}
