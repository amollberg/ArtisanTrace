import TestUtils.Companion.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2
import java.io.File

class SvgTest {
    @Test
    fun interfaceEnds() {
        val svg = Svg.fromFile(File("src/test/resources/IC1.svg"))
        assertEquals(
            listOf(
                listOf(Vector2(8.0, 10.0), Vector2(8.0, 25.0)),
                listOf(Vector2(22.0, 10.0), Vector2(22.0, 25.0))
            ),
            svg.interfaceEnds,
            delta = 1e-5
        )
    }
}
