import coordinates.Coordinate
import coordinates.Length
import org.openrndr.KeyModifier.*
import org.openrndr.MouseEvent
import org.openrndr.math.Vector2
import kotlin.math.max

class InterfaceMoveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedItf: Interface? = null
    var mouseOffset = Length(Vector2(0.0, 0.0), viewModel.root)
    internal val interfaceSelector = MouseHoverInterfaceSelector(viewModel)
    internal val snapper = InterfaceSnapSubtool(viewModel)
    var hasSelectedItf = false

    override fun mouseClicked(position: Coordinate) {
        if (!hasSelectedItf) {
            // Select the nearest interface
            selectedItf = interfaceSelector.getInterface()
            selectedItf?.ifPresent {
                hasSelectedItf = true
                mouseOffset = viewModel.mousePoint - it.center
            }
        } else {
            // Place the selected interface
            selectedItf?.ifPresent { update(it) }
            hasSelectedItf = false
            selectedItf = null
        }
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        val itf = selectedItf ?: return

        if (mouse.modifiers.contains(SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        } else if (mouse.modifiers.contains(ALT)) {
            var count = itf.terminalCount
            count += mouse.rotation.y.toInt()
            count = max(1, count)

            val highestOccupied =
                (0 until itf.terminalCount).lastOrNull { i ->
                    itf.getConnectedSegments(i, viewModel.model).isNotEmpty()
                } ?: -1
            count = max(highestOccupied + 1, count)
            itf.terminalCount = count
        } else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        interfaceSelector.draw(drawer)

        val itf = selectedItf ?: return
        update(itf)

        itf.draw(drawer)
    }

    internal fun update(itf: Interface) {
        // Interfaces that are in SVG component systems shall not be movable
        // with this tool
        if (itf.center.system == viewModel.root) {
            // Snap the interface to a group member bound if alt is held
            val doSnap = CTRL in viewModel.modifierKeysHeld
            snapper.updateSnapTarget(itf, doSnap)
            if (doSnap) {
                itf.center = snapper.getSnappedPosition(itf)
            }
            // Restrict the new interface position if shift is held
            else if (SHIFT in viewModel.modifierKeysHeld) {
                // Note: A temporary variable is created here because itf.center
                // needs to be untouched while projectOrthogonal is called below
                var newCenter = viewModel.mousePoint - mouseOffset
                newCenter = projectOrthogonal(newCenter, itf.getTerminals())
                itf.center = newCenter
            } else {
                itf.center = viewModel.mousePoint - mouseOffset
            }
        }
    }
}
