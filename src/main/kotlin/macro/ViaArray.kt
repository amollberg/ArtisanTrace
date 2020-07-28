import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Segment

fun SvgMacro.ViaArray.draw(drawer: CompositionDrawer) {
    val length = (terminals - 1) * pitch
    val start = Vector2.ZERO
    val end = Vector2(length, 0.0)

    drawSvgInterface(drawer, LineSegment(start, end), terminals)
    Segment(start, end).equidistantPositions(terminals)
        .forEach {
            drawer.circle(it, 5.0)
        }
}
