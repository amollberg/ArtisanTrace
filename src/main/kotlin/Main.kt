import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import java.io.File

class ViewModel(internal var model: Model) {
    var mousePoint = Vector2(-1.0, -1.0)
    var activeTool: BaseTool = EmptyTool(this)

    // Map KEY_CODE to whether the key is held or not
    var modifierKeysHeld = HashMap<Int, Boolean>()
    var areInterfacesVisible = false

    fun keyUp(key: KeyEvent) {
        updateModifiers(key)
        when (key.name) {
            "q" -> {
                changeTool(EmptyTool(this))
            }
            "w" -> {
                changeTool(TraceDrawTool(this))
            }
            "e" -> {
                changeTool(InterfaceDrawTool(this))
            }
            "x" -> {
                toggleInterfaceVisibility()
            }
            "d" -> {
                changeTool(InterfaceInsertTool(this))
            }
            "r" -> {
                changeTool(InterfaceTraceDrawTool(this))
            }
            "t" -> {
                changeTool(InterfaceMoveTool(this))
            }
            "s" -> {
                model.saveToFile()
            }
        }
    }

    private fun toggleInterfaceVisibility() {
        areInterfacesVisible = !areInterfacesVisible
    }

    fun draw(drawer: Drawer) {
        model.draw(drawer, areInterfacesVisible)
        activeTool.draw(drawer)
    }

    internal fun changeTool(newTool: BaseTool) {
        activeTool.exit()
        activeTool = newTool
    }

    fun keyDown(key: KeyEvent) {
        updateModifiers(key)
    }

    fun fileDrop(
        drop: DropEvent,
        fileOpenedClosure: (loadedFile: File) -> Unit
    ) {
        val fileOpened = drop.files.first()
        if (modifierKeysHeld.getOrDefault(KEY_LEFT_SHIFT, false)) {
            // Add the model from the file as a subcomponent
            var submodel = Model.loadFromFile(fileOpened)
            if (submodel != null) {
                model.components.add(
                    Component(submodel, Transform(translation = drop.position))
                )
            }
        } else {
            // Replace the top level model
            var replacingModel = Model.loadFromFile(fileOpened)
            if (replacingModel != null) {
                model = replacingModel
            }
            fileOpenedClosure(fileOpened)
        }
    }

    private fun updateModifiers(key: KeyEvent) {
        modifierKeysHeld[key.key] = key.type == KeyEventType.KEY_DOWN
    }
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        var viewModel = ViewModel(modelFromFileOrDefault(Model()))

        window.title = "ArtisanTrace"

        window.drop.listen {
            viewModel.fileDrop(it) { loadedFile ->
                window.title = "ArtisanTrace - ${loadedFile.name}"
            }
        }
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
        keyboard.keyDown.listen {
            viewModel.keyDown(it)
        }
        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            viewModel.draw(drawer)
        }
    }
}

fun modelFromFileOrDefault(defaultModel: Model) =
    Model.loadFromFile(Model().backingFile) ?: defaultModel
