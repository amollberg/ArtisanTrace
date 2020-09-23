import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.Segment

fun SvgMacro.IntegratedCircuit.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK

    val height = pinPitch * (pinsPerSide - 1 + 2 * pinCornerMargin)
    drawer.rectangle(0.0, 0.0, width.value, height)

    drawPins(
        drawer, Segment(
            Vector2(0.0, 0.0),
            Vector2(0.0, height)
        ), pinsPerSide.value, pinLength, pinCornerMargin
    )

    // Pins are drawn on the right side as seen walking from start to end, so
    // walk in negative Y direction to place them outside the right rectangle
    // edge
    drawPins(
        drawer, Segment(
            Vector2(width.value, height),
            Vector2(width.value, 0.0)
        ), pinsPerSide.value, pinLength, pinCornerMargin
    )
}

