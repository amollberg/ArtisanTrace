import coordinates.Coordinate
import org.openrndr.MouseEvent

class ConcaveAreaMacroTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val areaSelector = MouseHoverPolySelector(viewModel)
    private var startDirection = Direction(0)

    val macro = SelfContainedTraceMacro(viewModel.model, 7.0)

    override fun mouseClicked(position: Coordinate) {
        val area = areaSelector.getPoly() ?: return
        val walker = walker(area, viewModel.mousePoint)
        val previewModel = macro.generate(walker)
        previewModel.commit()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        startDirection += -mouse.rotation.y.toInt()
    }

    override fun draw(drawer: OrientedDrawer) {
        areaSelector.draw(drawer)
        val area = areaSelector.getPoly() ?: return
        val walker = walker(area, viewModel.mousePoint)
        val previewModel = macro.generate(walker)
        previewModel.draw(drawer)
    }

    private fun walker(area: Poly, startPoint: Coordinate): Walker {
        val grid = ArrayPolyGrid(
            area.rotated(startDirection.angle45 * 45.0),
            7.0
        )
        return SpiralWalker(
            grid,
            grid.position(startPoint),
            TurnDirection.LEFT
        )
    }
}
