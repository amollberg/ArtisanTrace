import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class InterfaceDrawTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private var itf = Interface(viewModel.mousePoint, 0.0, 20.0, 1)

    override fun mouseClicked(position: Vector2) {
        viewModel.interfaces.add(itf)
        itf = itf.clone()
    }

    override fun mouseScrolled(mouse: MouseEvent) {
        itf.angle -= mouse.rotation.y * 45 % 360
    }

    override fun draw(drawer: Drawer) {
        itf.center = viewModel.mousePoint
        itf.draw(drawer)
    }
}
