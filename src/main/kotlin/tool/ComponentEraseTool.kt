import coordinates.Coordinate

class ComponentEraseTool(viewModel: ViewModel) : BaseTool(viewModel) {
    internal val componentSelector = MouseHoverComponentSelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        val component = componentSelector.getComponent() ?: return
        when (component::class) {
            SketchComponent::class -> {
                (component as SketchComponent).interfaces.forEach {
                    viewModel.model.eraseSegmentsTo(it)
                }
                viewModel.model.sketchComponents.remove(component)
            }
            SvgComponent::class -> {
                (component as SvgComponent).interfaces.forEach {
                    viewModel.model.eraseSegmentsTo(it)
                }
                viewModel.model.svgComponents.remove(component)
            }
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        componentSelector.draw(drawer)
    }
}
