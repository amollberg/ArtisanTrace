import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SelfContainedTraceMacroTest : WithImplicitView() {
    @Test
    fun threeByThreeRect() {
        // Note: Indicating positive Y start direction
        val macro = SelfContainedTraceMacro(view.model, 10.0, Direction(-2))
        val surface = Surface(Poly.rect(view.root, 20, 20), emptySet())

        macro.generate(surface.poly, view.root.coord(Vector2(10.0, 10.0)))
            .commit()

        // The leftward spiral prefers going left over straight ahead so it
        // will go in positive X
        assertEquals(
            listOf(
                Vector2(10.0, 10.0),
                Vector2(20.0, 10.0),
                Vector2(20.0, 0.0),
                Vector2(0.0, 0.0),
                Vector2(0.0, 20.0),
                Vector2(20.0, 20.0)
            ), view.model.traces.first().terminals
                .map { it.hostInterface.center.xyIn(view.root) }
        )
    }

    @Test
    fun irregularQuad() {
        val macro = SelfContainedTraceMacro(view.model, 10.0, Direction(0))
        val surface = Surface(Poly(
            listOf(
                Vector2(30.0, 20.0),
                Vector2(10.0, 90.0),
                Vector2(80.0, 70.0),
                Vector2(50.0, 0.0)
            ).map { view.root.coord(it) }
        ), emptySet())

        // Does not throw an exception
        macro.generate(surface.poly, view.root.coord(Vector2(32.0, 21.0)))
            .commit()
    }
}
