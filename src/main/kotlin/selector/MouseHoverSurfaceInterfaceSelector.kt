class MouseHoverSurfaceInterfaceSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val itf = getInterface() ?: return
        itf.getTerminals().range.forEach { i ->
            markTerminal(drawer, itf, i)
        }
    }

    private fun interfaceOnSurface(itf: Interface, surface: Poly) =
        surface.segmentPointers.any {
            it.segment(itf.center.system)
                .project(itf.center.xy())
                .distance == 0.0
        }

    fun getInterface(): Interface? {
        val interfaces =
            viewModel.model.getInterfacesRecursively().filter { itf ->
                viewModel.model.groups.any {
                    interfaceOnSurface(
                        itf,
                        it.surface
                    )
                }
            }

        return interfaces.minBy {
            (it.center - viewModel.mousePoint).lengthIn(viewModel.root)
        }
    }
}
