class MouseHoverInterfaceSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            markTerminal(drawer, itf, i)
        }
    }

    fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.model.getInterfacesRecursively().minBy {
            (it.center - viewModel.mousePoint).lengthIn(viewModel.root)
        }
    }
}
