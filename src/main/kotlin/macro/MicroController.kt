import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.Segment

fun SvgMacro.MicroController.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK

    val sideLength = pinPitch * pinsPerSide
    drawer.rectangle(0.0, 0.0, sideLength, sideLength)
    drawPins(
        drawer,
        Segment(Vector2(0.0, 0.0), Vector2(0.0, sideLength)),
        pinsPerSide.value,
        pinLength,
        pinCornerMargin
    )
    drawPins(
        drawer,
        Segment(Vector2(0.0, sideLength), Vector2(sideLength, sideLength)),
        pinsPerSide.value,
        pinLength,
        pinCornerMargin
    )
    drawPins(
        drawer,
        Segment(Vector2(sideLength, sideLength), Vector2(sideLength, 0.0)),
        pinsPerSide.value,
        pinLength,
        pinCornerMargin
    )
    drawPins(
        drawer,
        Segment(Vector2(sideLength, 0.0), Vector2(0.0, 0.0)),
        pinsPerSide.value,
        pinLength,
        pinCornerMargin
    )
}
