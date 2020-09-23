import org.openrndr.color.ColorRGBa.Companion.BLACK
import org.openrndr.color.ColorRGBa.Companion.GREEN
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.LineSegment

fun SvgMacro.ZigZagEnd.draw(drawer: CompositionDrawer) {
    drawer.stroke = GREEN
    drawer.fill = BLACK

    val stride = 5.0
    fun positionOf(terminal: Int) =
        Vector2(0.0, 1.0 * stride * terminal)

    val terminalPairs = terminals / 2
    (0 until terminalPairs).forEach { pair ->
        val startTerminal = pair * 2
        drawer.lineSegment(
            positionOf(startTerminal),
            positionOf(startTerminal + 1)
        )
    }
    drawSvgInterface(
        drawer,
        LineSegment(positionOf(0), positionOf(terminals - 1)),
        terminals.value
    )
}
