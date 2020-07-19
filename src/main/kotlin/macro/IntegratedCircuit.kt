import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.LineSegment
import org.openrndr.shape.Segment

fun SvgMacro.IntegratedCircuit.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK

    val height = pinPitch * (pinsPerSide - 1 + 2 * pinCornerMargin)
    drawer.rectangle(0.0, 0.0, width, height)
    listOf(0.0, width).forEach { x ->
        fun tipFrom(stemPosition: Vector2) =
            if (x == 0.0) stemPosition - Vector2(pinLength, 0.0)
            else stemPosition + Vector2(pinLength, 0.0)

        val side = Segment(
            Vector2(x, pinPitch * pinCornerMargin),
            Vector2(x, height - pinPitch * pinCornerMargin)
        )
        val stems = side.equidistantPositions(pinsPerSide)
        drawer.lineSegments(stems.map { stem ->
            LineSegment(stem, tipFrom(stem))
        })

        val itfEnds = listOf(stems.first(), stems.last()).map { tipFrom(it) }
        drawSvgInterface(
            drawer, LineSegment(itfEnds.first(), itfEnds.last()), pinsPerSide
        )
    }
}
