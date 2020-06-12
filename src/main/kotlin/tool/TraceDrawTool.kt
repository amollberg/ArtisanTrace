import coordinates.Coordinate
import org.openrndr.MouseEvent
import org.openrndr.math.clamp

class TraceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val trace = Trace()
    private var previousTerminals: Terminals? = null
    private val terminalSelector = MouseHoverTerminalSelector(viewModel)
    private val angle = Angle.OBTUSE
    private var hasPlacedStart = false

    override fun mouseClicked(position: Coordinate) {
        val clickedTerminals = terminalSelector.getTerminals() ?: return
        if (!hasPlacedStart) {
            // Note: previousTerminals is assigned at the end of this function
            hasPlacedStart = true
        } else {
            trace.add(
                TraceSegment(
                    previousTerminals!!, clickedTerminals, angle
                )
            )
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
        } else {
            // Change the match order of the destination terminals
            terminalSelector.reverseTerminalOrder =
                !terminalSelector.reverseTerminalOrder
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        terminalSelector.draw(drawer)

        if (hasPlacedStart) {
            val selectedEndTerminals =
                terminalSelector.getTerminals() ?: return
            val s =
                TraceSegment(previousTerminals!!, selectedEndTerminals, angle)
            trace.withSegment(s).draw(drawer)
        }
    }

    override fun exit() {
        if (trace.segments.size > 0) {
            viewModel.model.traces.add(trace)
        }
    }
}
