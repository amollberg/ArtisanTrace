import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class ComponentMoveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedComponent: Component? = null
    var mouseOffset = Vector2.ZERO
    internal val componentSelector = MouseHoverComponentSelector(viewModel)
    var hasSelectedComponent = false

    override fun mouseClicked(position: Vector2) {
        if (!hasSelectedComponent) {
            // Select the component under the mouse
            selectedComponent = componentSelector.getComponent() ?: return
            hasSelectedComponent = true
            mouseOffset = viewModel.mousePoint - selectedComponent!!.origin()
        } else {
            // Place the selected component (already done in draw())
            hasSelectedComponent = false
            selectedComponent = null
        }
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        val component = selectedComponent ?: return

        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            component.transform.scale += 0.2 * mouse.rotation.y
        } else {
            component.transform.rotation -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: Drawer) {
        componentSelector.draw(drawer)

        val component = selectedComponent ?: return
        component.transform.translation = viewModel.mousePoint - mouseOffset
    }
}
