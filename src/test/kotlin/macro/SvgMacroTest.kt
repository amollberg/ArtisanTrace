import TestUtils.Companion.dropFiles
import TestUtils.Companion.tempAtgFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2

class SvgMacroTest : WithImplicitView() {

    @Test
    fun deserializeVerticalPins() {
        SvgMacro.deserialize(
            """{"type": "SvgMacro.VerticalPins", "pins": 2}"""
        )
    }

    @Test
    fun serializeDefaultVerticalPins() {
        SvgMacro.VerticalPins().serialize()
    }

    @Test
    fun loadVerticalPinsViaView() {
        dropFiles(
            view, DropEvent(
                Vector2.ZERO, listOf(
                    tempAtgFile(
                        """{"type": "SvgMacro.VerticalPins", "pins":2}"""
                    )
                )
            )
        )

        assertEquals(1, view.model.svgComponents.size)
        assertEquals(2, view.model.svgComponents.first().interfaces.size)
    }

    @Test
    fun loadRectGridViaView() {
        dropFiles(
            view, DropEvent(
                Vector2.ZERO, listOf(
                    tempAtgFile(
                        """{"type": "SvgMacro.RectGrid", "countX": 4}"""
                    )
                )
            )
        )

        assertEquals(1, view.model.svgComponents.size)
    }
}
