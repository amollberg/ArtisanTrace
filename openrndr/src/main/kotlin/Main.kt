import org.openrndr.KeyEvent
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

class ViewModel {
    var interfaces: MutableList<Interface> = mutableListOf()
    var mousePoint = Vector2(-1.0, -1.0)
    var traces : MutableList<Trace> = mutableListOf()
    var activeTool : BaseTool = EmptyTool(this)

    var areInterfacesVisible = true

    fun keyUp(key : KeyEvent) {
        when (key.name) {
            "q" -> { changeTool(EmptyTool(this)) }
            "w" -> { changeTool(TraceDrawTool(this)) }
            "e" -> { changeTool(InterfaceDrawTool(this)) }
            "x" -> { toggleInterfaceVisibility() }
            "d" -> { changeTool(InterfaceInsertTool(this)) }
            "r" -> { changeTool(InterfaceTraceDrawTool(this)) }
        }
    }

    private fun toggleInterfaceVisibility() {
        areInterfacesVisible = !areInterfacesVisible
    }

    fun draw(drawer: Drawer) {
        traces.forEach { it -> it.draw(drawer) }
        if (areInterfacesVisible) {
            interfaces.forEach { it -> it.draw(drawer) }
        }
        activeTool.draw(drawer)
    }

    private fun changeTool(newTool : BaseTool) {
        activeTool.exit()
        activeTool = newTool
    }
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        var viewModel = ViewModel()

        mouse.moved.listen {
            viewModel.mousePoint = it.position
        }
        mouse.clicked.listen {
            viewModel.activeTool.mouseClicked(it.position)
        }
        mouse.scrolled.listen {
            viewModel.activeTool.mouseScrolled(it)
        }
        keyboard.keyUp.listen {
            viewModel.keyUp(it)
        }
        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            viewModel.draw(drawer)
        }
    }
}
