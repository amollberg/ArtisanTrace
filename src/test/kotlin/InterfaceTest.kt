import coordinates.Coordinate
import coordinates.Coordinate.Companion.zeroIn
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal class InterfaceTest {
    private val system = root()

    @Test
    fun getEnds() {
        val itf = Interface(
            Coordinate(Vector2(100.0, 200.0), system), -45.0, 10.0, 3
        )
        assertEquals(
            listOf(
                Vector2(
                    100 - 5 * cos(-45 * PI / 180),
                    200 - 5 * sin(-45 * PI / 180)
                ),
                Vector2(
                    100 + 5 * cos(-45 * PI / 180),
                    200 + 5 * sin(-45 * PI / 180)
                )
            ),
            itf.getEnds().map { it.xyIn(system) }
        )
    }

    @Test
    fun withTerminalCount() {
        assertEquals(
            20.0,
            interfaceWith(20.0, 1).withTerminalCount(1).length
        )

        assertEquals(
            4.0,
            interfaceWith(6.0, 4).withTerminalCount(3).length
        )

        assertEquals(
            1.0,
            interfaceWith(3.0, 4).withTerminalCount(1).length
        )

        assertEquals(
            1.0,
            interfaceWith(3.0, 4).withTerminalCount(2).length
        )
    }

    private fun interfaceWith(length: Double, count: Int) =
        Interface(zeroIn(system), 0.0, length, count)
}
