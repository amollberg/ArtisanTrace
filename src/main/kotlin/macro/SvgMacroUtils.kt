import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Segment

fun drawSvgInterface(
    drawer: CompositionDrawer,
    segment: LineSegment,
    terminalCount: Int
) {
    isolatedStyle(
        drawer,
        stroke = interfaceKeyColor(terminalCount)
    ) {
        drawer.lineSegment(segment)
    }
}

// Draw pins on the right side of the segment as seen when walking from
// start to end
fun drawPins(
    drawer: CompositionDrawer,
    segment: Segment,
    pins: Int,
    pinLength: Double,
    pinCornerMargin: Double
) {
    fun tipFrom(stemPosition: Vector2) =
        stemPosition + segment.direction().rotate(90.0) * pinLength

    val cornerMargin = pinCornerMargin / (pins - 1 + 2 * pinCornerMargin)
    val insideMargin = segment.sub(cornerMargin, 1 - cornerMargin)
    val stems = insideMargin.equidistantPositions(pins)
    drawer.lineSegments(stems.map { stem ->
        val tip = tipFrom(stem)
        LineSegment(stem, tip)
    })

    val itfEnds = listOf(stems.first(), stems.last()).map { tipFrom(it) }
    drawSvgInterface(
        drawer,
        LineSegment(
            itfEnds.first(),
            itfEnds.last()
        ),
        pins
    )
}
