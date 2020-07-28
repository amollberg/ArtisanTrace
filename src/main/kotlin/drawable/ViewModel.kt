import SvgUtils.Companion.addBlackBackground
import coordinates.Coordinate
import coordinates.Oriented
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonException
import kotlinx.serialization.json.contentOrNull
import org.openrndr.DropEvent
import org.openrndr.KeyEvent
import org.openrndr.KeyModifier
import org.openrndr.KeyModifier.SHIFT
import org.openrndr.color.ColorRGBa
import org.openrndr.color.ColorRGBa.Companion.BLACK
import org.openrndr.color.ColorRGBa.Companion.GREEN
import org.openrndr.draw.Drawer
import org.openrndr.events.Event
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.svg.writeSVG
import tool.ColorPickTool
import java.io.File
import java.io.FileNotFoundException

class ViewModel(internal var model: Model) {
    val root = model.system
    var mousePoint = root.coord(Vector2(-1.0, -1.0))
    var activeTool: BaseTool = EmptyTool(this)
    var extendedVisualization = false
    val modelLoaded = Event<File>("model-loaded")

    // For unit testing and debugging
    var muteSerializationExceptions = true

    var modifierKeysHeld = setOf<KeyModifier>()

    companion object {
        val DEFAULT_STYLE = Style(
            fill = ColorRGBa.WHITE,
            stroke = ColorRGBa.PINK,
            strokeWeight = 1.2,
            background = ColorRGBa.BLACK
        )
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
                toggleExtendedVisualization()
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
                model.exportToSvg()
            }
            "y" -> {
                changeTool(ComponentMoveTool(this))
            }
            "f" -> {
                model.inferSvgInterfaces()
            }
            "a" -> {
                changeTool(InterfaceEraseTool(this))
            }
            "g" -> {
                changeTool(ColorPickTool(this))
            }
            "u" -> {
                changeTool(ComponentEraseTool(this))
            }
            "j" -> {
                changeTool(TraceSegmentEraseTool(this))
            }
            "k" -> {
                changeTool(GroupAssignTool(this))
            }
            "m" -> {
                changeTool(ConcaveAreaMacroTool(this))
            }
            "p" -> {
                changeTool(PolyDrawTool(this))
            }
            else -> {
                activeTool.keyUp(key)
            }
        }
    }

    private fun toggleExtendedVisualization() {
        extendedVisualization = !extendedVisualization
    }

    fun draw(drawer: Drawer) {
        val orientedDrawer = OrientedDrawer(
            compositionDrawer(drawer), root,
            extendedVisualization
        )

        val interfacesToIgnore = if (!extendedVisualization) {
            model.connectedInterfaces()
        } else {
            setOf()
        }

        model.draw(orientedDrawer, interfacesToIgnore)
        activeTool.draw(orientedDrawer)
        drawer.composition(orientedDrawer.drawer.composition)
    }

    internal fun changeTool(newTool: BaseTool) {
        activeTool.exit()
        activeTool = newTool
    }

    fun keyDown(key: KeyEvent) {
        updateModifiers(key)
    }

    private fun updateModifiers(key: KeyEvent) {
        modifierKeysHeld = key.modifiers.toMutableSet()
    }

    fun fileDrop(drop: DropEvent) {
        drop.files.forEach { droppedFile ->
            when (droppedFile.extension) {
                "svg" ->
                    handleDroppedSvgFile(droppedFile, root.coord(drop.position))
                "atg" ->
                    handleDroppedMacroFile(
                        droppedFile,
                        root.coord(drop.position)
                    )
                else ->
                    // Treat as a sketch file containing a model
                    handleDroppedSketchFile(
                        droppedFile,
                        root.coord(drop.position)
                    )
            }
        }
    }

    private fun handleDroppedMacroFile(droppedFile: File, coord: Coordinate) {
        val jsonRoot = Json(JsonConfiguration.Default)
            .parseJson(droppedFile.readText())
        val obj = jsonRoot.jsonObject
        val ob = obj.content["type"]
        val typeString = ob?.contentOrNull.orEmpty()
        println("$obj, $ob, $typeString")
        when (typeString.split('.')[0]) {
            "SvgMacro" -> handleDroppedSvgMacroFile(droppedFile, coord)
            "SketchMacro" -> handleDroppedSketchMacroFile(droppedFile, coord)
        }
    }

    private fun handleDroppedSvgMacroFile(
        droppedFile: File,
        coord: Coordinate
    ) {
        val content = droppedFile.readText()
        val obj = maybeMuteExceptions {
            SvgMacro.json.parse(SvgMacro.serializer(), content)
        }
        obj?.ifPresent {
            val svgFile = File(droppedFile.absoluteFile.path + ".svg")
            val cd = CompositionDrawer()
            cd.stroke = GREEN
            cd.fill = BLACK
            when (obj) {
                is SvgMacro.RectGrid -> obj.draw(cd)
                is SvgMacro.VerticalPins -> obj.draw(cd)
                is SvgMacro.IntegratedCircuit -> obj.draw(cd)
                is SvgMacro.MicroController -> obj.draw(cd)
                is SvgMacro.ZigZagEnd -> obj.draw(cd)
                is SvgMacro.ViaArray -> obj.draw(cd)
            }
            val svgText = addBlackBackground(writeSVG(cd.composition))
            svgFile.writeText(svgText)
            model.addSvg(svgFile, coord)
        }
    }

    private fun handleDroppedSvgFile(droppedFile: File, position: Coordinate) {
        // Add the svg from the file as a subcomponent
        maybeMuteExceptions { model.addSvg(droppedFile, position) }
    }

    private fun handleDroppedSketchMacroFile(
        droppedFile: File, coord: Coordinate
    ) {
        val content = droppedFile.readText()
        val obj = maybeMuteExceptions {
            SketchMacro.json.parse(SketchMacro.serializer(), content)
        }
        obj?.ifPresent {
            val sketchFile = File(droppedFile.absoluteFile.path + ".ats")
            val sketchModel = Model()
            sketchModel.backingFile = sketchFile
            when (obj) {
                is SketchMacro.ObverseIcTrace -> obj.create(sketchModel)
            }
            sketchModel.saveToFile()
            model.addSketch(sketchFile, coord)!!
        }
    }

    private fun handleDroppedSketchFile(
        droppedFile: File,
        position: Coordinate
    ) {
        if (SHIFT in modifierKeysHeld) {
            // Add the model from the file as a subcomponent
            maybeMuteExceptions { model.addSketch(droppedFile, position) }
        } else {
            // Replace the top level model
            val fileOpened = droppedFile.absoluteFile
            var replacingModel =
                maybeMuteExceptions { Model.loadFromFile(fileOpened) }
            replacingModel?.ifPresent {
                model = it
                modelLoaded.trigger(fileOpened)
            }
        }
    }

    private fun <T> maybeMuteExceptions(code: () -> T) =
        if (muteSerializationExceptions) {
            try {
                code()
            } catch (e: JsonException) {
                println(e)
                null
            } catch (e: SerializationException) {
                println(e)
                null
            } catch (e: FileNotFoundException) {
                println(e)
                null
            }
        } else {
            code()
        }
}

fun isolatedStyle(
    drawer: CompositionDrawer,
    fill: ColorRGBa? = drawer.fill,
    stroke: ColorRGBa? = drawer.stroke,
    strokeWeight: Double = drawer.strokeWeight,
    action: (drawer: CompositionDrawer) -> Unit
) {
    val oldFill = drawer.fill
    val oldStroke = drawer.stroke
    val oldStrokeWeight = drawer.strokeWeight

    drawer.fill = fill
    drawer.stroke = stroke
    drawer.strokeWeight = strokeWeight

    action(drawer)

    drawer.fill = oldFill
    drawer.stroke = oldStroke
    drawer.strokeWeight = oldStrokeWeight
}

data class Style(
    val fill: ColorRGBa?,
    val stroke: ColorRGBa?,
    val strokeWeight: Double,
    val background: ColorRGBa
)

fun setStyle(drawer: Drawer, style: Style) {
    drawer.fill = style.fill
    drawer.stroke = style.stroke
    drawer.strokeWeight = style.strokeWeight
    drawer.clear(style.background)
}

fun setStyle(drawer: CompositionDrawer, style: Style) {
    drawer.fill = style.fill
    drawer.stroke = style.stroke
    drawer.strokeWeight = style.strokeWeight
}

data class OrientedDrawer(
    val drawer: CompositionDrawer,
    override val system: coordinates.System,
    val extendedVisualization: Boolean
) : Oriented {
}

fun compositionDrawer(drawer: Drawer): CompositionDrawer {
    val cd = CompositionDrawer()
    cd.fill = drawer.fill
    cd.stroke = drawer.stroke
    cd.strokeWeight = drawer.strokeWeight
    return cd
}
