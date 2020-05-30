import org.openrndr.draw.Drawer
import org.openrndr.shape.Shape

class MouseHoverComponentSelector(private val viewModel: ViewModel) {

    fun draw(drawer: Drawer) {
        val component = getComponent() ?: return
        drawer.shape(Shape(listOf(component.bounds())))
    }

    fun getComponent(): Component? =
        // Get the interface under the mouse
        viewModel.model.components.firstOrNull {
            it.bounds().contains(viewModel.mousePoint)
        }
}
