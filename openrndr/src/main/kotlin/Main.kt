@file:UseSerializers(Vector2Serializer::class)
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import org.openrndr.KeyEvent
import org.openrndr.KeyEventType
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import java.io.File

val json = Json(JsonConfiguration.Stable.copy())

@Serializable
class ViewModel {
    var interfaces: MutableList<Interface> = mutableListOf()
    @Transient
    var mousePoint = Vector2(-1.0, -1.0)
    var traces : MutableList<Trace> = mutableListOf()
    @Transient
    var activeTool : BaseTool = EmptyTool(this)
    // Map KEY_CODE to whether the key is held or not
    @Transient
    var modifierKeysHeld = HashMap<Int, Boolean>()

    var areInterfacesVisible = true

    companion object {
        fun loadFromFile(): ViewModel? {
            val file = File("sketch.cts")
            if (!file.isFile) return null
            return try {
                json.parse(ViewModel.serializer(), file.readText())
            }
            catch (e: JsonException) { null }
            catch (e: SerializationException) { null }
        }
    }

    fun keyUp(key : KeyEvent) {
        updateModifiers(key)
        when (key.name) {
            "q" -> { changeTool(EmptyTool(this)) }
            "w" -> { changeTool(TraceDrawTool(this)) }
            "e" -> { changeTool(InterfaceDrawTool(this)) }
            "x" -> { toggleInterfaceVisibility() }
            "d" -> { changeTool(InterfaceInsertTool(this)) }
            "r" -> { changeTool(InterfaceTraceDrawTool(this)) }
            "t" -> { changeTool(InterfaceMoveTool(this)) }
            "s" -> { saveToFile() }
        }
    }

    private fun saveToFile() {
        File("sketch.cts").writeText(
            json.stringify(ViewModel.serializer(),this))
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

    fun keyDown(key: KeyEvent) {
        updateModifiers(key)
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
