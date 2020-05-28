import org.openrndr.KEY_LEFT_SHIFT
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.clamp

class InterfaceTraceDrawTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {
    private val trace = Trace()
    private var previousTerminals: Terminals? = null
    internal val terminalSelector = MouseHoverTerminalSelector(viewModel)
    private val angle = Angle.OBTUSE
    private var hasPlacedStart = false

    override fun mouseClicked(position: Vector2) {
        if (!hasPlacedStart) {
            // Attach to existing interface to begin with
            val clickedTerminals = terminalSelector.getTerminals() ?: return
            previousTerminals = clickedTerminals
            hasPlacedStart = true

            // Update interface properties from clicked interface
            itf = interfaceLikeNearest(position).withTerminalCount(
                terminalSelector.desiredLeads
            )
        }
        else {
            // Place an interface and connect the trace to it
            trace.add(
                TraceSegment(
                    previousTerminals!!,
                    itf.getTerminals(),
                    angle
                )
            )
            previousTerminals = itf.getTerminals()
            viewModel.model.interfaces.add(itf)
            itf = itf.clone()
        }
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
        else {
            // Behave like any other interface tool
            super.mouseScrolled(mouse)
        }
    }

    override fun draw(drawer: Drawer) {
        itf.center = viewModel.mousePoint

        if (!hasPlacedStart) {
            terminalSelector.draw(drawer)
        }
        else {
            // Restrict the new interface position if shift is held
            if (viewModel.modifierKeysHeld.getOrDefault(
                    KEY_LEFT_SHIFT,
                    false
                )
            ) {
                itf.center = projectOrthogonal(itf.center, previousTerminals!!)
            }

            val s = TraceSegment(previousTerminals!!, itf.getTerminals(), angle)
            itf.draw(drawer)
            trace.withSegment(s).draw(drawer)
        }
    }

    override fun exit() {
        viewModel.model.traces.add(trace)
    }
}
