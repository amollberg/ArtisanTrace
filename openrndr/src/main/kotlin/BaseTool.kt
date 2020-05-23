import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

abstract class BaseTool(var viewModel : ViewModel) {

    open fun mouseClicked(position: Vector2) {}

    open fun draw(drawer : Drawer) {}

    open fun exit() {}
}
