import coordinates.Coordinate

class InterfaceEraseTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {
    private val interfaceSelector = MouseHoverInterfaceSelector(
        viewModel,
        // Not possible to erase interfaces from components
        includeComponents = false
    )

    override fun mouseClicked(position: Coordinate) {
        val itf = interfaceSelector.getInterface() ?: return

        viewModel.model.eraseSegmentsTo(itf)
        viewModel.model.interfaces.remove(itf)
    }

    override fun draw(drawer: OrientedDrawer) {
        interfaceSelector.draw(drawer)
    }
}
