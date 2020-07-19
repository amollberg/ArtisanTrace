import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.Segment

class IntegratedCircuitTest {
    @Test
    fun generateFourPinsPerSideAtCorrectLocations() {
        val ic = SvgMacro.IntegratedCircuit(
            pinsPerSide = 4,
            pinPitch = 2.0,
            pinLength = 3.0,
            pinCornerMargin = 0.75,
            width = 10.0
        )
        val expectedTerminalPositions = listOf(
            listOf(
                Vector2(-3.0, 1.5),
                Vector2(-3.0, 3.5),
                Vector2(-3.0, 5.5),
                Vector2(-3.0, 7.5)
            ),
            listOf(
                Vector2(13.0, 1.5),
                Vector2(13.0, 3.5),
                Vector2(13.0, 5.5),
                Vector2(13.0, 7.5)
            )
        )
        val drawer = CompositionDrawer()
        ic.draw(drawer)
        val interfaces = drawer.composition.findShapes()
            .filter { sameRGB(it.effectiveStroke, INTERFACE_KEY_COLOR) }
            .flatMap {
                it.shape.contours.flatMap {
                    it.segments.map {
                        InferredInterface(listOf(it.start, it.end), 4)
                    }
                }
            }
        val terminalPositions = interfaces.map {
            Segment(it.ends.first(), it.ends.last()).equidistantPositions(4)
        }
        assertEquals(expectedTerminalPositions, terminalPositions)
    }
}
