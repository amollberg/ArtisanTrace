import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import org.openrndr.math.Vector2
import kotlin.math.max

open class BaseInterfaceTool(viewModel: ViewModel) : BaseTool(viewModel) {
    protected var itf = interfaceLikeNearest(viewModel.mousePoint)

    // Copy-pasted from InterfaceDrawTool.mouseScrolled
    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            itf.length += 4 * mouse.rotation.y
        }
        else if (mouse.modifiers.contains(KeyModifier.ALT)) {
            itf.terminalCount += mouse.rotation.y.toInt()
            itf.terminalCount = max(1, itf.terminalCount)
        }
        else {
            itf.angle -= mouse.rotation.y * 45 % 360
        }
    }

    private fun getNearestInterface(position: Vector2): Interface? {
        return viewModel.interfaces.minBy { (it.center - position).length }
    }

    private fun interfaceLikeNearest(position: Vector2): Interface {
        val nearestItf = getNearestInterface(position) ?:
                         return Interface(position, 0.0, 20.0, 1)
        return nearestItf.clone()
    }
}
