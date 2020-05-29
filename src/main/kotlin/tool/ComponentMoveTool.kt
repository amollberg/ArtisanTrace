import coordinates.Coordinate
import coordinates.Length
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
            // Place the selected component (already done in draw())
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
        componentSelector.draw(drawer)

        val component = selectedComponent ?: return
        component.system.originCoord = viewModel.mousePoint - mouseOffset!!
    }
}
