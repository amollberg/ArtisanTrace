import TestUtils.Companion.assertListEquals
import org.junit.jupiter.api.Test
import org.openrndr.math.Vector2

class SelfContainedTraceMacroTest : WithImplicitView() {
    @Test
    fun threePointsWalked() {
        // Note: Indicating positive Y start direction
        val macro = SelfContainedTraceMacro(view.model)
        val walker = object : Walker {
            override fun generate() =
                Path(
                    listOf(
                        Vector2(10.0, 10.0),
                        Vector2(20.0, 10.0),
                        Vector2(20.0, 0.0)
                    ).map { GridPosition(it, view.root) }.toMutableList()
                )
        }
        macro.generate(walker).commit()

        assertListEquals(
            listOf(
                Vector2(10.0, 10.0),
                Vector2(20.0, 10.0),
                Vector2(20.0, 0.0)
            ), view.model.traces.first().terminals
                .map { it.hostInterface.center.xyIn(view.root) }
        )
        assertListEquals(
            listOf(
                Vector2(10.0, 10.0),
                Vector2(20.0, 0.0)
            ),
            view.model.svgInterfaces.map { it.center.xyIn(view.root) })
    }

    @Test
    fun irregularQuad() {
        val macro = SelfContainedTraceMacro(view.model)
        val surface = Surface(Poly(
            listOf(
                Vector2(30.0, 20.0),
                Vector2(10.0, 90.0),
                Vector2(80.0, 70.0),
                Vector2(50.0, 0.0)
            ).map { view.root.coord(it) }
        ), emptySet())
        val grid = ArrayPolyGrid(surface.poly, 10.0)
        val walker =
            ZigZagWalker(grid, grid.position(2, 0), TurnDirection.RIGHT)

        // Does not throw an exception
        macro.generate(walker).commit()
    }
}
