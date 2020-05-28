@file:UseSerializers(Vector2Serializer::class)

import kotlinx.serialization.UseSerializers
import org.openrndr.DropEvent
import org.openrndr.KeyEvent
import org.openrndr.KeyEventType
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

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
        model.traces.forEach { it -> it.draw(drawer) }

        if (areInterfacesVisible) {
            model.interfaces
        } else {
            onlyUnconnectedInterfaces()
        }.forEach { it -> it.draw(drawer) }

        activeTool.draw(drawer)
    }

    internal fun changeTool(newTool: BaseTool) {
        activeTool.exit()
        activeTool = newTool
    }

    fun keyDown(key: KeyEvent) {
        updateModifiers(key)
    }

    fun fileDrop(it: DropEvent) {
        var replacingModel = Model.loadFromFile(it.files.first())
        if (replacingModel != null) {
            model = replacingModel
        }
    }

    private fun updateModifiers(key: KeyEvent) {
        modifierKeysHeld[key.key] = key.type == KeyEventType.KEY_DOWN
    }

    /** Return all interfaces that are not connected to a trace */
    private fun onlyUnconnectedInterfaces(): Set<Interface> {
        return model.interfaces.toSet() - model.traces.flatMap {
            it.segments.map {
                it.getStart().hostInterface
            }
        }.toSet() - model.traces.flatMap {
            it.segments.map {
                it.getEnd().hostInterface
            }
        }.toSet()
    }
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        var viewModel = ViewModel(modelFromFileOrDefault(Model()))

        window.drop.listen {
            viewModel.fileDrop(it)
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
