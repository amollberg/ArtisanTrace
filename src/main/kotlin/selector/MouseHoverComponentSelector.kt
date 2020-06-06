import org.openrndr.shape.Shape

class MouseHoverComponentSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val component = getComponent() ?: return
        drawer.drawer.shape(Shape(listOf(component.bounds(drawer.system))))
    }

    fun getComponent(): Component? =
        // Get the interface under the mouse
        viewModel.model.components.firstOrNull {
            it.bounds(viewModel.mousePoint.system)
                .contains(viewModel.mousePoint.xy())
        }
}