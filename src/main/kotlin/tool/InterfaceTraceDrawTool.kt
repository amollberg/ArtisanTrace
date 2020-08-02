import coordinates.Coordinate
import org.openrndr.KeyModifier.CTRL
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.MouseEvent
import org.openrndr.math.clamp

class InterfaceTraceDrawTool(viewModel: ViewModel) :
    BaseInterfaceTool(viewModel) {
    private val trace = Trace(viewModel.root)
    private var previousTerminals: Terminals? = null
    internal val terminalSelector = MouseHoverTerminalSelector(viewModel)
    internal val snapper = InterfaceSnapSubtool(viewModel)
    private val angle = Angle.OBTUSE
    private var hasPlacedStart = false
    private var reverseKnee = false


    override fun mouseClicked(position: Coordinate) {
        if (!hasPlacedStart) {
            // Attach to existing interface to begin with
            val clickedTerminals = terminalSelector.getTerminals() ?: return
            previousTerminals = clickedTerminals
            hasPlacedStart = true

            // Update interface properties from clicked interface
            itf = interfaceLikeNearest(position).withTerminalCount(
                terminalSelector.desiredLeads
            )
        } else {
            update()
            // Place an interface and connect the trace to it
            trace.add(
                TraceSegment(
                    previousTerminals!!,
                    itf.getTerminals(),
                    angle,
                    reverseKnee,
                    trace.system
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
        } else if (mouse.modifiers.contains(CTRL)) {
            // Reverse the knee if Control key is held
            reverseKnee = !reverseKnee
        } else {
            // Behave like any other interface tool
            super.mouseScrolled(mouse)
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        if (!hasPlacedStart) {
            terminalSelector.draw(drawer)
        } else {
            update()

            val s = TraceSegment(
                previousTerminals!!,
                itf.getTerminals(),
                angle,
                reverseKnee,
                trace.system
            )
            itf.draw(drawer)
            trace.withSegment(s).draw(drawer)
        }
    }

    private fun update() {
        itf.center = viewModel.mousePoint
        // Snap the interface to a group member bound if alt is held
        val doSnap = CTRL in viewModel.modifierKeysHeld
        snapper.updateSnapTarget(itf, doSnap)
        if (doSnap) {
            itf.center = snapper.getSnappedPosition(itf)
        }
        // Restrict the new interface position if shift is held
        else if (SHIFT in viewModel.modifierKeysHeld) {
            itf.center = projectOrthogonal(itf.center, previousTerminals!!)
        }
    }

    override fun exit() {
        if (trace.segments.size > 0) {
            viewModel.model.traces.add(trace)
        }
    }
}
