class MouseHoverInterfaceSelector(
    private val viewModel: ViewModel,
    private val includeComponents: Boolean = true
) {

    fun draw(drawer: OrientedDrawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            markTerminal(drawer, itf, i)
        }
    }

    fun getInterface(): Interface? {
        val interfaces = if (includeComponents) {
            viewModel.model.getInterfacesRecursively()
        } else {
            viewModel.model.interfaces
        }
        // Get the interface nearest to the mouse
        return interfaces.minBy {
            (it.center - viewModel.mousePoint).lengthIn(viewModel.root)
        }
    }
}
