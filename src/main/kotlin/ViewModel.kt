import coordinates.Coordinate
import coordinates.Oriented
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonException
import org.openrndr.DropEvent
import org.openrndr.KEY_LEFT_SHIFT
import org.openrndr.KeyEvent
import org.openrndr.KeyEventType
import org.openrndr.draw.Drawer
import org.openrndr.events.Event
import org.openrndr.math.Vector2
import org.openrndr.svg.loadSVG
import java.io.File

class ViewModel(internal var model: Model) {
    val root = model.system
    var mousePoint = root.coord(Vector2(-1.0, -1.0))
    var activeTool: BaseTool = EmptyTool(this)
    var areInterfacesVisible = false
    val modelLoaded = Event<File>("model-loaded")

    // For unit testing and debugging
    var muteSerializationExceptions = true

    // Map KEY_CODE to whether the key is held or not
    var modifierKeysHeld = HashMap<Int, Boolean>()

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
            "y" -> {
                changeTool(ComponentMoveTool(this))
            }
        }
    }

    private fun toggleInterfaceVisibility() {
        areInterfacesVisible = !areInterfacesVisible
    }

    fun draw(drawer: Drawer) {
        val orientedDrawer = OrientedDrawer(drawer, root)
        model.draw(orientedDrawer, areInterfacesVisible)
        activeTool.draw(orientedDrawer)
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

    fun fileDrop(drop: DropEvent) {
        drop.files.forEach { droppedFile ->
            when (droppedFile.extension) {
                "svg" ->
                    handleDroppedSvgFile(droppedFile, root.coord(drop.position))
                else ->
                    // Treat as a sketch file containing a model
                    handleDroppedSketchFile(
                        droppedFile,
                        root.coord(drop.position)
                    )
            }
        }
    }

    private fun handleDroppedSvgFile(droppedFile: File, position: Coordinate) {
        // Add the svg from the file as a subcomponent
        val fileOpened =
            model.backingFile.toPath().toAbsolutePath().parent
                .relativize(droppedFile.toPath())
                .toFile()
        if (fileOpened.isFile) {
            model.svgComponents.add(
                SvgComponent(
                    Svg(loadSVG(fileOpened.path), fileOpened),
                    root.createSystem(origin = position.xyIn(root))
                )
            )
        }
    }

    private fun handleDroppedSketchFile(
        droppedFile: File,
        position: Coordinate
    ) {
        if (modifierKeysHeld.getOrDefault(KEY_LEFT_SHIFT, false)) {
            // Add the model from the file as a subcomponent
            val fileOpened =
                model.backingFile.toPath().toAbsolutePath().parent
                    .relativize(droppedFile.toPath()).toFile()
            var submodel =
                if (muteSerializationExceptions) {
                    try {
                        Model.loadFromFile(fileOpened)
                    } catch (e: JsonException) {
                        null
                    } catch (e: SerializationException) {
                        null
                    }
                } else {
                    Model.loadFromFile(fileOpened)
                }

            if (submodel != null) {
                model.sketchComponents.add(
                    SketchComponent(
                        submodel,
                        root.createSystem(origin = position.xyIn(root))
                    )
                )
            }
        } else {
            // Replace the top level model
            val fileOpened = droppedFile.absoluteFile
            var replacingModel = Model.loadFromFile(fileOpened)
            if (replacingModel != null) {
                model = replacingModel
                modelLoaded.trigger(fileOpened)
            }
        }
    }
}

data class OrientedDrawer(
    val drawer: Drawer,
    override val system: coordinates.System
) : Oriented