import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import kotlin.math.max
import kotlin.math.min

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()
    private var previousPoint : Vector2? = null
    private var previousTerminals : Terminals? = null
    private val terminalSelector = MouseHoverTerminalSelector(viewModel)
    private val angle = Angle.OBTUSE
    private var hasPlacedStart = false

    override fun mouseClicked(position : Vector2) {
        val clickedTerminals = terminalSelector.getTerminals() ?: return
        if (!hasPlacedStart) {
            // Note: previousTerminals is assigned at the end of this function
            hasPlacedStart = true
        }
        else {
            trace.add(TraceSegment(
                previousTerminals!!, clickedTerminals, angle))
        }
        previousTerminals = clickedTerminals
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        if (!hasPlacedStart) {
            // Update number of leads to be placed
            var leads = terminalSelector.desiredLeads
            leads += mouse.rotation.y.toInt()
            leads = leads.clamp(
                1,
                terminalSelector.getInterface()?.terminalCount ?: leads
            )
            terminalSelector.desiredLeads = leads
        }
    }


    override fun draw(drawer : Drawer) {
        terminalSelector.draw(drawer)

        if (hasPlacedStart) {
            val selectedEndTerminals =
                terminalSelector.getTerminals() ?: return
            val s = TraceSegment(
                previousTerminals!!, selectedEndTerminals, angle)
            trace.withSegment(s).draw(drawer)
        }
    }

    override fun exit() {
        viewModel.traces.add(trace)
    }
}
