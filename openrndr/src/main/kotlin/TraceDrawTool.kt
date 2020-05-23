import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()
    private var previousPoint : Vector2? = null
    private val angle = Angle.OBTUSE

    override fun mouseClicked(position : Vector2) {
        if (previousPoint != null) {
            trace.add(TraceSegment(previousPoint!!, position, angle))
        }
        previousPoint = position
    }

    override fun draw(drawer : Drawer) {
        if (previousPoint != null) {
            val s = TraceSegment(previousPoint!!, viewModel.mousePoint, angle)
            trace.withSegment(s).draw(drawer)
        }
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }
}
