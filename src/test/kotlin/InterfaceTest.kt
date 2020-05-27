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
}
