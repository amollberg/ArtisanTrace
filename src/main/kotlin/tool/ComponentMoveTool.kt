import coordinates.Coordinate
import coordinates.Length
import org.openrndr.KeyEvent
import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import kotlin.math.PI

class ComponentMoveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedComponent: Component? = null
    var mouseOffset: Length? = null
    internal val componentSelector = MouseHoverComponentSelector(viewModel)
    var hasSelectedComponent = false

    override fun mouseClicked(position: Coordinate) {
        if (!hasSelectedComponent) {
            // Select the component under the mouse
            selectedComponent = componentSelector.getComponent() ?: return
            hasSelectedComponent = true
            mouseOffset =
                viewModel.mousePoint - selectedComponent!!.system.originCoord
        } else {
            updatePosition()
            // Place the selected component
            hasSelectedComponent = false
            selectedComponent = null
        }
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        val component = selectedComponent ?: return

        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            val scaleChange = 0.2 * mouse.rotation.y
            component.system.axes *= (1 + scaleChange)
        } else {
            val degrees = -mouse.rotation.y * 45 % 360
            component.system.axes *= Matrix22.rotation(degrees * PI / 180)
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        if (!hasSelectedComponent) {
            componentSelector.draw(drawer)
        }
        updatePosition()
    }

    override fun keyUp(key: KeyEvent) {
        if (key.name == "c") {
            val component = componentSelector.getComponent() ?: return
            // Create a copy of the nearest component and select it
            selectedComponent = component.clone(viewModel.model)
            hasSelectedComponent = true
            mouseOffset =
                viewModel.mousePoint - selectedComponent!!.system.originCoord
        }
    }

    private fun updatePosition() {
        val component = selectedComponent ?: return
        component.system.originCoord = viewModel.mousePoint - mouseOffset!!
    }
}
