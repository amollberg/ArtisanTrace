import coordinates.Coordinate
import org.openrndr.KeyEvent
import org.openrndr.MouseEvent

abstract class BaseTool(var viewModel: ViewModel) {

    open fun mouseClicked(position: Coordinate) {}

    open fun mouseRightClicked(position: Coordinate) {}

    open fun mouseScrolled(mouse: MouseEvent) {}

    open fun draw(drawer: OrientedDrawer) {}

    open fun keyUp(key: KeyEvent) {}

    open fun exit() {}
}
