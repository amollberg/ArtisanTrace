import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.math.max

class InterfaceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private var itf = Interface(viewModel.mousePoint, 0.0, 20.0, 1)

    override fun mouseClicked(position: Vector2) {
        viewModel.interfaces.add(itf)
        itf = itf.clone()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        }
        else if (mouse.modifiers.contains(KeyModifier.ALT)) {
            itf.terminals += mouse.rotation.y.toInt()
            itf.terminals = max(1, itf.terminals)
        }
        else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    override fun draw(drawer: Drawer) {
        itf.center = viewModel.mousePoint
        itf.draw(drawer)
    }
}
