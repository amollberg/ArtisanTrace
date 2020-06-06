import org.openrndr.MouseEvent
import org.openrndr.draw.Drawer
import org.openrndr.shape.CompositionDrawer

abstract class BaseSelection(var viewModel: ViewModel) {

    open fun mouseScrolled(mouse: MouseEvent) {}

    open fun getTerminals(): Terminals? = null

    open fun getInterface(): Interface? = null

    open fun draw(drawer: Drawer) {}
}

fun markTerminal(
    drawer: OrientedDrawer, itf: Interface, terminalIndex:
    Int
) {
    isolatedStyle(drawer.drawer, strokeWeight = 1.0) { d ->
        d.circle(
            itf.getTerminalPosition(terminalIndex)
                .xyIn(drawer.system),
            4.0
        )
    }
}
