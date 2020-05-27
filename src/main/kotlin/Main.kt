@file:UseSerializers(Vector2Serializer::class)

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import org.openrndr.KeyEvent
import org.openrndr.KeyEventType
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import java.io.File

val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

@Serializable
class ViewModel {
    var interfaces: MutableList<Interface> = mutableListOf()

    @Transient
    var mousePoint = Vector2(-1.0, -1.0)
    var traces: MutableList<Trace> = mutableListOf()

    @Transient
    var activeTool: BaseTool = EmptyTool(this)

    // Map KEY_CODE to whether the key is held or not
    @Transient
    var modifierKeysHeld = HashMap<Int, Boolean>()

    var areInterfacesVisible = false

    companion object {
        fun loadFromFile(): ViewModel? {
            val file = File("sketch.cts")
            if (!file.isFile) return null
            return deserialize(file.readText())
        }

        internal fun deserialize(string: String): ViewModel? {
            return try {
                postProcessDeserialized(
                    json.parse(ViewModel.serializer(), string)
                )
            } catch (e: JsonException) {
                null
            } catch (e: SerializationException) {
                null
            }

        }

        private fun postProcessDeserialized(viewModel: ViewModel): ViewModel {
            viewModel.traces.forEach {
                it.segments.forEach {
                    it.start =
                        replaceInterfaceUsingViewModel(it.start, viewModel)
                    it.end = replaceInterfaceUsingViewModel(it.end, viewModel)
                }
            }
            return viewModel
        }

        /** Use the ID to replace the interface with the correct instance
         *  from the view model interface list.
         */
        private fun replaceInterfaceUsingViewModel(
            terminals: Terminals, viewModel: ViewModel
        ): Terminals {
            val id = terminals.hostInterface.id
            return Terminals(viewModel.interfaces.first {
                it.id == id
            }, terminals.range)
        }
    }

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
                saveToFile()
            }
        }
    }

    private fun saveToFile() {
        interfaces.forEachIndexed { i, itf -> itf.id = i }
        File("sketch.cts").writeText(serialize())
    }

    internal fun serialize(): String {
        return json.stringify(ViewModel.serializer(), this)
    }

    private fun toggleInterfaceVisibility() {
        areInterfacesVisible = !areInterfacesVisible
    }

    fun draw(drawer: Drawer) {
        traces.forEach { it -> it.draw(drawer) }

        if (areInterfacesVisible) {
            interfaces
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

    private fun updateModifiers(key: KeyEvent) {
        modifierKeysHeld[key.key] = key.type == KeyEventType.KEY_DOWN
    }

    /** Return all interfaces that are not connected to a trace */
    private fun onlyUnconnectedInterfaces(): Set<Interface> {
        return interfaces.toSet() - traces.flatMap {
            it.segments.map {
                it.getStart().hostInterface
            }
        }.toSet() - traces.flatMap {
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
        var viewModel = modelFromFileOrDefault(ViewModel())

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

fun modelFromFileOrDefault(defaultModel: ViewModel) =
    ViewModel.loadFromFile() ?: defaultModel
