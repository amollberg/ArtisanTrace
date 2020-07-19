import TestUtils.Companion.assertListListEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SvgTest {
    @Test
    fun interfaceEnds() {
        val svgPath = "src/test/resources/IC1.svg"
        val svg = Svg.fromFile(File(svgPath))
        assertListListEquals(
            EXPECTED_INTERFACES[svgPath]!!.map { it.ends },
            svg.interfaces.map { it.ends },
            delta = 1e-5
        )
    }

    @Test
    fun terminalCount() {
        val svgPath = "src/test/resources/IC1.svg"
        val svg = Svg.fromFile(File(svgPath))
        assertEquals(
            EXPECTED_INTERFACES[svgPath]!!.map { it.terminalCount },
            svg.interfaces.map { it.terminalCount }
        )
    }

    @Test
    fun loadInkscapeSvg() {
        Svg.fromFile(File("src/test/resources/Via1.svg"))
        Svg.fromFile(File("src/test/resources/Via2.svg"))
    }

    @Test
    fun terminalCountsVsColor() {
        listOf(1, 2, 17, 255).forEach { terminalCount ->
            assertEquals(
                terminalCount,
                colorToTerminalCount(interfaceKeyColor(terminalCount))
            )
        }
    }
}
