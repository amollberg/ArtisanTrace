class MouseHoverInterfaceSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            drawer.drawer.circle(
                itf.getTerminalPosition(i).xyIn(drawer.system),
                6.0
            )
        }
    }

    fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.model.interfaces.minBy {
            (it.center - viewModel.mousePoint).xyIn(viewModel.root).length
        }
    }
}
