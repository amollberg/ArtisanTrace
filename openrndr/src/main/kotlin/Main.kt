import org.openrndr.KeyEvent
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

abstract class BaseSelection(var viewModel: ViewModel) {

    open fun getInterface(): Interface? = null

    open fun draw(drawer: Drawer) {}
}

class MouseHoverSelection(viewModel: ViewModel) : BaseSelection(viewModel) {
    private val selectedInterface: Interface? = null

    override fun getInterface(): Interface? {
        // Get the interface nearest to the mouse
        return viewModel.interfaces.minBy {
            (it.center - viewModel.mousePoint).length
        }
    }

    override fun draw(drawer: Drawer) {
        val selectedInterface = getInterface()
        if (selectedInterface != null) {
            drawer.circle(Circle(selectedInterface.center, 8.0))
        }
    }
}

class ViewModel {
    var interfaces: MutableList<Interface> = mutableListOf()
    var mousePoint = Vector2(-1.0, -1.0)
    var traces : MutableList<Trace> = mutableListOf()
    var activeTool : BaseTool = EmptyTool(this)
    var activeSelection : BaseSelection = MouseHoverSelection(this)

    fun keyUp(key : KeyEvent) {
        when (key.name) {
            "q" -> { changeTool(EmptyTool(this)) }
            "w" -> { changeTool(TraceDrawTool(this)) }
            "e" -> { changeTool(InterfaceDrawTool(this)) }
        }
    }

    fun draw(drawer: Drawer) {
        traces.forEach { it -> it.draw(drawer) }
        interfaces.forEach { it -> it.draw(drawer) }
        activeSelection.draw(drawer)
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
