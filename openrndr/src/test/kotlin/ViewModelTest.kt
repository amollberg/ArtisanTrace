import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.openrndr.KeyModifier
import org.openrndr.MouseButton
import org.openrndr.MouseEvent
import org.openrndr.MouseEventType
import org.openrndr.math.Vector2

class ViewModelTest {

    @Test
    fun deserializeSerialized() {
        val original = createViewModel()
        assertEquals(original, ViewModel.deserialize(original.serialize())!!)
    }

    @Test
    fun deserializeTwice() {
        val original = createViewModel()
        val serializedOnce = ViewModel.deserialize(original.serialize())!!
        val serializedTwice =
            ViewModel.deserialize(serializedOnce.serialize())!!
        assertEquals(original, serializedTwice)
    }

    @Test
    fun serializeTwice() {
        val original = createViewModel()
        val serializedOnce = ViewModel.deserialize(original.serialize())!!
        assertEquals(original.serialize(), serializedOnce.serialize())
    }

    @Test
    fun modifyAfterDeserialization() {
        var original = createViewModel()
        var serializedOnce = ViewModel.deserialize(original.serialize())!!
        assertEquals(
            modifyViewModel(original),
            modifyViewModel(serializedOnce))
    }

    @Test
    fun internalSanityCheckModifiedViewModel() {
        assertNotEquals(createViewModel(), modifyViewModel(createViewModel()))
    }

    private fun createViewModel(): ViewModel {
        var original = ViewModel()

        original.changeTool(InterfaceDrawTool(original))
        original.activeTool.mouseScrolled(MouseEvent(
            Vector2.ZERO, Vector2(0.0, 3.0), Vector2.ZERO,
            MouseEventType.SCROLLED, MouseButton.NONE, setOf(KeyModifier.ALT), false))
        original.activeTool.mouseClicked(Vector2(47.0, 11.0))
        original.activeTool.mouseClicked(Vector2(300.0, 11.0))

        // Restore to default tool before any serialization
        original.activeTool = EmptyTool(original)
        return original
    }

    private fun modifyViewModel(original: ViewModel): ViewModel {
        original.changeTool(InterfaceTraceDrawTool(original))
        original.activeTool.mouseScrolled(MouseEvent(
            Vector2.ZERO, Vector2(0.0, 3.0), Vector2.ZERO,
            MouseEventType.SCROLLED, MouseButton.NONE, setOf(), false))
        original.activeTool.mouseClicked(Vector2(47.0, 11.0))
        original.activeTool.mouseClicked(Vector2(100.0, 111.0))

        original.changeTool(EmptyTool(original))
        return original
    }
}
