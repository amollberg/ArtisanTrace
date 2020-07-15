import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.contour
import kotlin.math.PI

fun SvgMacro.VerticalPins.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK
    val sideWithMargin = pinSize * (1 + margin)
    val innerRect = Vector2(sideWithMargin * (pins - 1), sideWithMargin)
    drawer.contour(contour {
        moveOrLineTo(0.0, 0.0)
        moveOrLineTo(innerRect.x, 0.0)
        arcTo(
            sideWithMargin / 2,
            sideWithMargin / 2,
            PI,
            true,
            true,
            innerRect
        )
        moveOrLineTo(0.0, innerRect.y)
        arcTo(
            sideWithMargin / 2,
            sideWithMargin / 2,
            PI,
            true,
            true,
            Vector2.ZERO
        )
    })
    drawer.fill = drawer.stroke
    drawer.circles((0 until pins).map {
        Vector2(
            it * sideWithMargin,
            sideWithMargin / 2
        )
    }, pinSize / 2.0)
}
