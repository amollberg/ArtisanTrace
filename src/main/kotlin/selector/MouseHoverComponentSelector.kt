import coordinates.System
import org.openrndr.shape.Shape

class MouseHoverComponentSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val component = getComponent() ?: return
        drawer.drawer.shape(
            Shape(
                listOf(
                    expandedContour(component, drawer.system)
                )
            )
        )
    }

    fun getComponent(): Component? =
        // Get the interface under the mouse
        viewModel.model.components.firstOrNull {
            expandedContour(it, viewModel.mousePoint.system)
                .contains(viewModel.mousePoint.xy())
        }

    private fun expandedContour(component: Component, system: System) =
        component.bounds.contour(system).offset(3.0)
}
