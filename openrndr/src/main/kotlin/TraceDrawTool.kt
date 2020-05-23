import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()

    override fun mouseClicked(position : Vector2) {
        trace.points.add(position)
    }

    override fun draw(drawer : Drawer) {
        trace.withPoint(viewModel.mousePoint).draw(drawer)
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }
}
