import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.openrndr.*
import org.openrndr.math.Vector2
import java.io.File

class ViewModelTest {

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

    private fun createModel(): Model {
        var original = ViewModel(Model(root()))

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
        original.activeTool.mouseClicked(at(original, 47.0, 11.0))
        original.activeTool.mouseClicked(at(original, 300.0, 11.0))

        original.fileDrop(
            DropEvent(
                Vector2(123.0, 45.0),
                listOf(File("src/test/resources/IC1.svg").absoluteFile)
            )
        )

        // Exit the active tool to commit any pending changes
        original.activeTool = EmptyTool(original)
        return original.model
    }

    private fun modifyModel(original: Model): Model {
        var viewModel = ViewModel(original)
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
        viewModel.activeTool.mouseClicked(at(viewModel, 47.0, 11.0))
        viewModel.activeTool.mouseClicked(at(viewModel, 100.0, 111.0))

        // Exit the active tool to commit any pending changes
        viewModel.changeTool(EmptyTool(viewModel))
        return original
    }

    private fun at(viewModel: ViewModel, x: Double, y: Double) =
        viewModel.root.coord(Vector2(x, y))

    private fun deserialize(value: String): Model =
        Model.deserialize(value, File("dontcare"))!!
}
