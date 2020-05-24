import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer

abstract class BaseSelection(var viewModel: ViewModel) {

    open fun mouseScrolled(mouse: MouseEvent) {}

    open fun getTerminals(): Terminals? = null

    open fun getInterface(): Interface? = null

    open fun draw(drawer: Drawer) {}
}
