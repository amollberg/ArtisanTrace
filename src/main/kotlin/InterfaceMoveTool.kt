import org.openrndr.KEY_LEFT_SHIFT
import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class InterfaceMoveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedItf: Interface? = null
    var mouseOffset = Vector2(0.0, 0.0)
    internal val interfaceSelector = MouseHoverInterfaceSelector(viewModel)
    var hasSelectedItf = false

    override fun mouseClicked(position: Vector2) {
        if (!hasSelectedItf) {
            // Select the nearest interface
            selectedItf = interfaceSelector.getInterface()
            if (selectedItf != null) {
                hasSelectedItf = true
                mouseOffset = viewModel.mousePoint - selectedItf!!.center
            }
        }
        else {
            // Place the selected interface
            // (already done in draw())
            hasSelectedItf = false
            selectedItf = null
        }
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        val itf = selectedItf ?: return

        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        }
        else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: Drawer) {
        interfaceSelector.draw(drawer)

        val itf = selectedItf ?: return
        // Note: A temporary variable is created here because itf.center needs to be untouched
        // while projectOrthogonal is called below
        var newCenter = viewModel.mousePoint - mouseOffset
        // Restrict the new interface position if shift is held
        if (viewModel.modifierKeysHeld
                .getOrDefault(KEY_LEFT_SHIFT, false) ) {
            newCenter = projectOrthogonal(newCenter, itf.getTerminals())
        }
        itf.center = newCenter

        itf.draw(drawer)
    }
}
