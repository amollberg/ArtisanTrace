import coordinates.Coordinate
import org.openrndr.KeyModifier
import org.openrndr.KeyModifier.ALT
import org.openrndr.MouseEvent
import kotlin.math.max

class InterfaceDrawTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {
    override fun mouseClicked(position: Coordinate) {
        updatePosition()
        viewModel.model.interfaces.add(itf)
        itf = itf.clone()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        } else if (mouse.modifiers.contains(ALT)) {
            itf.terminalCount += mouse.rotation.y.toInt()
            itf.terminalCount = max(1, itf.terminalCount)
        } else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        updatePosition()
        itf.draw(drawer)
    }

    private fun updatePosition() {
        itf.center = viewModel.mousePoint
    }
}
