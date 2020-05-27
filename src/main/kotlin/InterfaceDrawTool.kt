import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.math.max

class InterfaceDrawTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {
    override fun mouseClicked(position: Vector2) {
        viewModel.interfaces.add(itf)
        itf = itf.clone()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        } else if (mouse.modifiers.contains(KeyModifier.ALT)) {
            itf.terminalCount += mouse.rotation.y.toInt()
            itf.terminalCount = max(1, itf.terminalCount)
        } else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: Drawer) {
        itf.center = viewModel.mousePoint
        itf.draw(drawer)
    }
}
