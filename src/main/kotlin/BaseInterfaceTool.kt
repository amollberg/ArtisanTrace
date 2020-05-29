import coordinates.Coordinate
import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import kotlin.math.max

open class BaseInterfaceTool(viewModel: ViewModel) : BaseTool(viewModel) {
    protected var itf = interfaceLikeNearest(viewModel.mousePoint)

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

    private fun getNearestInterface(position: Coordinate): Interface? {
        return viewModel.model.interfaces.minBy {
            (it.center - position).lengthIn(position.system)
        }
    }

    protected fun interfaceLikeNearest(position: Coordinate): Interface {
        val nearestItf = getNearestInterface(position) ?: return Interface(
            position,
            0.0,
            20.0,
            1
        )
        return nearestItf.clone()
    }
}
