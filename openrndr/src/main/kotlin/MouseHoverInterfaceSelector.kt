import org.openrndr.draw.Drawer

class MouseHoverInterfaceSelector(private val viewModel: ViewModel) {

    fun draw(drawer: Drawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            drawer.circle(itf.getTerminalPosition(i), 6.0)
        }
    }

    fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.interfaces.minBy {
            (it.center - viewModel.mousePoint).length
        }
    }
}
