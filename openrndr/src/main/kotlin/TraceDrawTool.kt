import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()
    private var previousPoint : Vector2? = null
    private val angle = Angle.OBTUSE

    override fun mouseClicked(position : Vector2) {
        trace.leads = requiredLeads()
        val connectPosition =
            viewModel.activeSelection.getInterface()?.center ?:
            position
        if (previousPoint != null) {
            trace.add(TraceSegment(previousPoint!!, connectPosition, angle))
        }
        previousPoint = connectPosition
    }

    override fun draw(drawer : Drawer) {
        trace.leads = requiredLeads()
        val connectPosition =
            viewModel.activeSelection.getInterface()?.center ?:
            viewModel.mousePoint
        if (previousPoint != null) {
            val s = TraceSegment(previousPoint!!, connectPosition, angle)
            trace.withSegment(s).draw(drawer)
        }
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }

    // TODO: Handle source interface too
    private fun requiredLeads() =
        viewModel.activeSelection.getInterface()?.getTerminals()?.count() ?:
        trace.leads
}
