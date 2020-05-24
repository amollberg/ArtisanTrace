import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.shape.Circle
import kotlin.math.max
import kotlin.math.min

/** On hover, select a contiguous range of terminals near the mouse */
class MouseHoverTerminalSelection(viewModel: ViewModel) : BaseSelection(viewModel) {
    var desiredLeads: Int = 1

    override fun mouseScrolled(mouse: MouseEvent) {
        desiredLeads += mouse.rotation.y.toInt()
        desiredLeads = max(1, desiredLeads)
        desiredLeads = min(desiredLeads, getInterface()?.terminalCount ?: desiredLeads)
    }

    override fun getTerminals(): Terminals? {
        val itf = getInterface() ?: return null
        val nearestIndices = itf.getTerminals().range.sortedBy {
            (itf.getTerminalPosition(it) - viewModel.mousePoint).length
        }.take(desiredLeads)
        return Terminals(itf, rangeOfList(nearestIndices))
    }

    override fun draw(drawer: Drawer) {
        val terminals = getTerminals() ?: return
        val selectedInterface = terminals.hostInterface
        terminals.range.forEach { i ->
            drawer.circle(selectedInterface.getTerminalPosition(i), 6.0)
        }
    }

    override fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.interfaces.minBy {
            (it.center - viewModel.mousePoint).length
        }
    }
}

fun rangeOfList(l: List<Int>): IntRange {
    return if (l.isNotEmpty()) {
        l.min()!! until (l.max()!! + 1)
    }
    else {
        IntRange.EMPTY
    }

}
