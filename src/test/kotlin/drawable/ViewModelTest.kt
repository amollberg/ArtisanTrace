import TestUtils.Companion.assertEquals
import TestUtils.Companion.assertNotEquals
import coordinates.System
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.openrndr.*
import org.openrndr.math.Vector2
import java.io.File

class ViewModelTest {

    companion object {
        val ORIGINAL_INTERFACE1_CENTER = Vector2(47.0, 11.0)
        val INTERFACE2_CENTER = Vector2(300.0, 11.0)
        val MOVED_INTERFACE1_CENTER = Vector2(100.0, 111.0)

        val ORIGINAL_COMP1_ORIGIN = Vector2(70.0, 200.0)
        val COMP2_ORIGIN = Vector2(200.0, 100.0)
    }

    @Test
    fun deserializeSerialized() {
        val original = createModel()
        assertEquals(original, deserialize(original.serialize()))
    }

    @Test
    fun deserializeTwice() {
        val original = createModel()
        val serializedOnce = deserialize(original.serialize())
        val serializedTwice = deserialize(serializedOnce.serialize())
        assertEquals(original, serializedTwice)
    }

    @Test
    fun serializeTwice() {
        val original = createModel()
        val serializedOnce = deserialize(original.serialize())
        assertEquals(original.serialize(), serializedOnce.serialize())
    }

    @Test
    fun modifyBeforeSerialization() {
        var original = modifyModel(createModel())
        var serializedOnce = deserialize(original.serialize())
        assertEquals(original, serializedOnce)
    }

    @Test
    fun modifyAfterDeserialization() {
        var original = createModel()
        var serializedOnce = deserialize(original.serialize())
        assertEquals(
            modifyModel(original),
            modifyModel(serializedOnce)
        )
    }

    @Test
    fun internalSanityCheckModifiedViewModel() {
        assertNotEquals(createModel(), modifyModel(createModel()))
    }

    @Test
    fun correctCoordinateSystemHierarchy() {
        var viewModel = createViewModel()
        checkAllStoredCoordinates(viewModel)

        (0..2).forEach { i ->
            println("Iteration $i")
            viewModel =
                createViewModel(deserialize(viewModel.model.serialize()))
            checkAllStoredCoordinates(viewModel)
        }
    }

    private fun createViewModel(model: Model = createModel()): ViewModel {
        val viewModel = ViewModel(model)
        viewModel.muteSerializationExceptions = false
        return viewModel
    }

    private fun createModel(): Model {
        var original = createViewModel(Model(root()))

        original.changeTool(InterfaceDrawTool(original))
        original.activeTool.mouseScrolled(
            MouseEvent(
                Vector2.ZERO,
                Vector2(0.0, 3.0),
                Vector2.ZERO,
                MouseEventType.SCROLLED,
                MouseButton.NONE,
                setOf(KeyModifier.ALT),
                false
            )
        )
        original.activeTool.mouseClicked(
            at(original, ORIGINAL_INTERFACE1_CENTER)
        )
        original.activeTool.mouseClicked(at(original, INTERFACE2_CENTER))

        original.fileDrop(
            DropEvent(
                Vector2(123.0, 45.0),
                listOf(File("src/test/resources/IC1.svg").absoluteFile)
            )
        )

        original.fileDrop(
            DropEvent(
                ORIGINAL_COMP1_ORIGIN,
                listOf(File("src/test/resources/IC1.ats").absoluteFile)
            )
        )

        original.fileDrop(
            DropEvent(
                COMP2_ORIGIN,
                listOf(File("src/test/resources/IC1.ats").absoluteFile)
            )
        )

        // Draw a trace between the components
        original.changeTool(TraceDrawTool(original))
        original.activeTool.mouseClicked(at(original, ORIGINAL_COMP1_ORIGIN))
        original.activeTool.mouseClicked(at(original, COMP2_ORIGIN))

        // Exit the active tool to commit any pending changes
        original.activeTool = EmptyTool(original)
        return original.model
    }

    private fun modifyModel(original: Model): Model {
        var viewModel = createViewModel(original)
        // Move one of the interfaces
        viewModel.changeTool(InterfaceTraceDrawTool(viewModel))
        viewModel.activeTool.mouseScrolled(
            MouseEvent(
                Vector2.ZERO,
                Vector2(0.0, 3.0),
                Vector2.ZERO,
                MouseEventType.SCROLLED,
                MouseButton.NONE,
                setOf(),
                false
            )
        )
        viewModel.activeTool.mouseClicked(
            at(viewModel, ORIGINAL_INTERFACE1_CENTER)
        )
        viewModel.activeTool.mouseClicked(
            at(viewModel, MOVED_INTERFACE1_CENTER)
        )

        // Move one of the components
        viewModel.changeTool(ComponentMoveTool(viewModel))
        viewModel.activeTool.mouseClicked(at(viewModel, ORIGINAL_COMP1_ORIGIN))
        viewModel.activeTool.mouseClicked(at(viewModel, ORIGINAL_COMP1_ORIGIN))

        // Exit the active tool to commit any pending changes
        viewModel.changeTool(EmptyTool(viewModel))
        return original
    }
}

private fun checkAllStoredCoordinates(model: Model) {
    val viewModel = ViewModel(model)
    viewModel.muteSerializationExceptions = false
    checkDescendant(viewModel.model, viewModel.root, "top-level model")
}

private fun checkAllStoredCoordinates(viewModel: ViewModel) {
    checkDescendant(viewModel.model, viewModel.root, "view model")
}

private fun checkDescendant(
    model: Model, rootSystem: System, context: String
) {
    checkDescendant(model.system, rootSystem, "model system of $context")
    model.traces.forEach { trace ->
        trace.segments.forEach {
            checkDescendant(
                it.start.hostInterface,
                rootSystem,
                "trace start interface in model of $context"
            )
            checkDescendant(
                it.getKnee(),
                rootSystem,
                "trace knee in model of $context"
            )
            checkDescendant(
                it.end.hostInterface,
                rootSystem,
                "trace end interface in model of $context"
            )
        }
    }
    model.interfaces.forEach {
        checkDescendant(it, rootSystem, "interface in model of $context")
    }
    model.svgComponents.forEach {
        checkDescendant(
            it.system,
            rootSystem,
            "svg component system in model of $context"
        )
    }
    model.sketchComponents.forEach {
        checkDescendant(
            it.system,
            rootSystem,
            "sketch component system of model of $context"
        )
        checkDescendant(
            it.model,
            rootSystem,
            "sketch component model system of model of $context"
        )
    }
}

private fun checkDescendant(
    itf: Interface, rootSystem: System, context: String
) {
    checkDescendant(itf.center, rootSystem, "center of $context")
}

/** Check that the coordinate is a descendant from the root system */
private fun checkDescendant(
    coordinate: coordinates.Coordinate,
    rootSystem: System,
    context: String
) {
    assertTrue(
        isDescendant(coordinate.system, rootSystem),
        "Coordinate ($context) $coordinate is not a descendant of $rootSystem"
    )
}

private fun checkDescendant(
    system: System,
    rootSystem: System,
    context: String
) {
    assertTrue(
        isDescendant(system, rootSystem),
        "System ($context) $system is not a descendant of $rootSystem"
    )
}

private fun isDescendant(system: System, rootSystem: System): Boolean {
    if (system === rootSystem) return true
    val reference = system.reference ?: return false
    return isDescendant(reference, rootSystem)
}

private fun at(viewModel: ViewModel, x: Double, y: Double) =
    viewModel.root.coord(Vector2(x, y))

private fun at(viewModel: ViewModel, xy: Vector2) =
    viewModel.root.coord(xy)

private fun deserialize(value: String): Model =
    Model.deserialize(value, File("dontcare"))!!
