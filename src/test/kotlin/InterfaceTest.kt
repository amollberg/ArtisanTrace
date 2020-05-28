import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal class InterfaceTest {

    @Test
    fun getEnds() {
        val itf = Interface(Vector2(100.0, 200.0), -45.0, 10.0, 3)
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
            itf.getEnds()
        )
    }

    @Test
    fun withTerminalCount() {
        assertEquals(
            20.0,
            Interface(Vector2.ZERO, 0.0, 20.0, 1)
                .withTerminalCount(1).length
        )

        assertEquals(
            4.0,
            Interface(Vector2.ZERO, 0.0, 6.0, 4)
                .withTerminalCount(3).length
        )

        assertEquals(
            1.0,
            Interface(Vector2.ZERO, 0.0, 3.0, 4)
                .withTerminalCount(1).length
        )

        assertEquals(
            1.0,
            Interface(Vector2.ZERO, 0.0, 3.0, 4)
                .withTerminalCount(2).length
        )
    }
}
