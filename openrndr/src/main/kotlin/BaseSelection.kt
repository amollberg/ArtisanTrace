import org.openrndr.draw.Drawer

abstract class BaseSelection(var viewModel: ViewModel) {

    open fun getTerminals(): Terminals? = null

    open fun getInterface(): Interface? = null

    open fun draw(drawer: Drawer) {}
}
