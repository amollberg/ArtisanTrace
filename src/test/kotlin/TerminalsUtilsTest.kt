import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class TerminalsUtilsTest {
    @Test
    fun projectOrthogonalZeroLength() {
        // Zero-length interface
        val itf = Interface(Vector2(x = 0.0), 0.0, 0.0, 5)
        val inputPosition = Vector2(17.0, 47.0)

        assertEquals(inputPosition,
                projectOrthogonal(inputPosition, itf.getTerminals()))
    }
}
