import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()
    private var doSnapTo45 = true

    override fun mouseClicked(position : Vector2) {
        trace.points.add(snappedPoint(position, doSnapTo45))
    }

    override fun draw(drawer : Drawer) {
        trace
            .withPoint(snappedPoint(viewModel.mousePoint, doSnapTo45))
            .draw(drawer)
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }

    private fun snappedPoint(position : Vector2, doSnapTo45 : Boolean) =
        if (doSnapTo45 && trace.points.isNotEmpty()) {
            snapTo45(trace.points.last(), position)
        } else {
            position
        }
}
