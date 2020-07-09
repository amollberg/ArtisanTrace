import org.openrndr.color.ColorRGBa

class MouseHoverPolySelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        isolatedStyle(drawer.drawer, fill = ColorRGBa.YELLOW) {
            getPoly()?.ifPresent { it.draw(drawer) }
        }
    }

    fun getPoly(): Poly? =
        viewModel.model.polys.firstOrNull { it.contains(viewModel.mousePoint) }
}
