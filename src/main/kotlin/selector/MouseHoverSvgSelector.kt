package selector

import OrientedDrawer
import ViewModel
import org.openrndr.shape.Shape

class MouseHoverSvgSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        val svgComponent = getSvgComponent() ?: return
        drawer.drawer.shape(Shape(listOf(svgComponent.bounds(drawer.system))))
    }

    fun getSvgComponent() =
        viewModel.model.getSvgComponentsRecursively().firstOrNull {
            it.bounds(viewModel.mousePoint.system)
                .contains(viewModel.mousePoint.xy())
        }
}
