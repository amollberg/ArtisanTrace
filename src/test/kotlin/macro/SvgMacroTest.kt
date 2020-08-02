import TestUtils.Companion.dropFiles
import TestUtils.Companion.tempAtgFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File

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
    fun loadVerticalPinsWithCorrectTerminalCounts() {
        dropFiles(
            view, DropEvent(
                Vector2.ZERO, listOf(
                    tempAtgFile(
                        """{"type": "SvgMacro.VerticalPins", "pins":5}"""
                    )
                )
            )
        )

        assertEquals(1, view.model.svgComponents.size)
        assertEquals(listOf(5, 5), view.model.svgComponents.first().interfaces
            .map { it.terminalCount })
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

    // Writes ATG files to components/ which will show up in the diff if changed
    @Test
    fun generateAllDefaults() {
        listOf(
            SvgMacro.IntegratedCircuit(),
            SvgMacro.MicroController(),
            SvgMacro.RectGrid(),
            SvgMacro.VerticalPins(),
            SvgMacro.ZigZagEnd(),
            SvgMacro.ViaArray()
        ).forEach {
            val className = it::class.simpleName!!.toLowerCase()
            val macroFile = File("components/${className}_default.atg")
            macroFile.writeText(it.serialize())
            dropFiles(view, DropEvent(Vector2.ZERO, listOf(macroFile)))
        }
    }
}
