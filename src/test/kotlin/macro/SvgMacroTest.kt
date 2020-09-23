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
            """{"type": "SvgMacro.VerticalPins", "pins": {
                                     "type": "IntOrRandom.Constant",
                                     "value": 2 }}"""
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
                        """{"type": "SvgMacro.VerticalPins", "pins": {
                                     "type": "IntOrRandom.Constant",
                                     "value": 2 }}"""
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
                        """{"type": "SvgMacro.VerticalPins", "pins":{
                                     "type": "IntOrRandom.Constant",
                                     "value": 5 }}"""
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
                        """{"type": "SvgMacro.RectGrid",
                                   "countX": {
                                     "type": "IntOrRandom.Constant",
                                     "value": 4 }}"""
                    )
                )
            )
        )

        assertEquals(1, view.model.svgComponents.size)
    }

    @Test
    fun generatedFilenameSuffix() {
        val rectGrid = SvgMacro.RectGrid(
            width = DoubleOrRandom.Constant(21.0),
            height = DoubleOrRandom.Constant(32.0),
            circleRadius = DoubleOrRandom.Constant(2.1),
            countX = IntOrRandom.Constant(3),
            countY = IntOrRandom.Constant(4),
            margin = 0.06
        )
        assertEquals(
            "RectGrid_width21.0_height32.0" +
                    "_circleRadius2.1_countX3_countY4_margin0.06",
            rectGrid.fileNameSuffix
        )
    }

    // Writes ATG files to components/ which will show up in the diff if changed
    @Test
    fun generateAllDefaults() {
        setOf(
            SvgMacro.IntegratedCircuit(),
            SvgMacro.MicroController(),
            SvgMacro.RectGrid(),
            SvgMacro.VerticalPins(),
            SvgMacro.ZigZagEnd(),
            SvgMacro.ViaArray()
        ).forEach {
            val className = it::class.simpleName!!.toLowerCase()
            val diffedMacroFile = File("components/${className}_default.atg")
            diffedMacroFile.writeText(it.serialize())
            val macroFile = File("build/components/${className}_default.atg")
            macroFile.mkdirs()
            diffedMacroFile.copyTo(macroFile, true)
            dropFiles(view, DropEvent(Vector2.ZERO, listOf(macroFile)))
        }
    }
}
