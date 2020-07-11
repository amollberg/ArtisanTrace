import coordinates.Coordinate
import org.openrndr.KeyModifier
import org.openrndr.MouseEvent
import java.lang.Math.floorMod

class ConcaveAreaMacroTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val areaSelector = MouseHoverPolySelector(viewModel)
    private var startDirection = Direction(0)
    private var selectedWalker = 0

    val macro = SelfContainedTraceMacro(viewModel.model, 7.0)

    override fun mouseClicked(position: Coordinate) {
        val area = areaSelector.getPoly() ?: return
        val walker = walker(area, viewModel.mousePoint)
        val previewModel = macro.generate(walker)
        previewModel.commit()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        if (mouse.modifiers.contains(KeyModifier.SHIFT)) {
            selectedWalker += mouse.rotation.y.toInt()
        } else {
            startDirection += -mouse.rotation.y.toInt()
        }
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
        val walkers = listOf(
            ZigZagWalker(
                grid,
                grid.position(startPoint),
                TurnDirection.LEFT
            ),
            SpiralWalker(
                grid,
                grid.position(startPoint),
                TurnDirection.LEFT
            )
        )
        return walkers[floorMod(selectedWalker, walkers.size)]
    }
}
