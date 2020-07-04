class MouseHoverSurfaceInterfaceSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            markTerminal(drawer, itf, i)
        }
    }

    fun getInterface(): Interface? {
        val interfaces =
            viewModel.model.groups.flatMap { it.surfaceInterfaces }

        return interfaces.minBy {
            (it.center - viewModel.mousePoint).lengthIn(viewModel.root)
        }
    }
}
