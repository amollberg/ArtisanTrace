import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.Rectangle
import org.openrndr.shape.Segment
import org.openrndr.shape.SegmentJoin.BEVEL
import kotlin.math.min

fun SvgMacro.BevelRectGrid.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK
    val marginAbs = min(width.value, height.value) * margin
    val rect = Rectangle(
        -Vector2(
            width.value / 2,
            height.value / 2
        ),
        width.value,
        height.value
    )
    drawer.contour(rect.contour.offset(marginAbs + bevelOffset, BEVEL))
    drawer.contour(rect.contour.offset(marginAbs, BEVEL))
    drawGrid(drawer)
    val outerRect = rect.offsetEdges(0.0, marginAbs + bevelOffset)
    drawPins(
        drawer,
        bottomSide(outerRect),
        pinsPerSide.value,
        0.001,
        pinCornerMargin
    )
    drawPins(
        drawer,
        topSide(outerRect),
        pinsPerSide.value,
        0.001,
        pinCornerMargin
    )
}

private fun SvgMacro.BevelRectGrid.drawGrid(drawer: CompositionDrawer) {
    val innerDim = Vector2(
        width.value - circleRadius.value * 2,
        height.value - circleRadius.value * 2
    )
    (0 until countX.value).forEach { xi ->
        (0 until countY.value).forEach { yi ->
            drawer.circle(
                -innerDim * 0.5 +
                        Vector2(
                            xi * innerDim.x / (countX.value - 1),
                            yi * innerDim.y / (countY.value - 1)
                        ), circleRadius.value
            )
        }
    }
}

fun topSide(rect: Rectangle) =
    Segment(rect.corner + Vector2(rect.width, 0.0), rect.corner)

fun bottomSide(rect: Rectangle) =
    Segment(
        rect.corner + Vector2(0.0, rect.height),
        rect.corner + Vector2(rect.width, rect.height)
    )
