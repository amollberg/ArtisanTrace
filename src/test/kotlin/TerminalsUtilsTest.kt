import TestUtils.Companion.assertEquals
import org.junit.jupiter.api.Test

class TerminalsUtilsTest : WithImplicitView() {
    @Test
    fun projectOrthogonalZeroLength() {
        // Zero-length interface
        val itf = Interface(at(x = 0.0), 0.0, 0.0, 5)
        val inputPosition = at(17.0, 47.0)

        assertEquals(
            inputPosition,
            projectOrthogonal(inputPosition, itf.getTerminals())
        )
    }
}
