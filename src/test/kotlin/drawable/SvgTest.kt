import TestUtils.Companion.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SvgTest {
    @Test
    fun interfaceEnds() {
        val svgPath = "src/test/resources/IC1.svg"
        val svg = Svg.fromFile(File(svgPath))
        assertEquals(
            EXPECTED_INTERFACE_ENDS[svgPath]!!,
            svg.interfaceEnds,
            delta = 1e-5
        )
    }
}
