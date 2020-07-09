import org.openrndr.color.ColorRGBa

class MouseHoverConcaveAreaSelector(private val viewModel: ViewModel) {

    fun draw(drawer: OrientedDrawer) {
        isolatedStyle(drawer.drawer, fill = ColorRGBa.YELLOW) {
            getArea()?.ifPresent { it.draw(drawer) }
        }
    }

    fun getArea(): Surface? =
        viewModel.model.groups.flatMap { it.surface.concaveAreas }
            .firstOrNull { it.poly.contains(viewModel.mousePoint) }
}
