import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.CompositionDrawer
import org.openrndr.shape.Rectangle
import kotlin.math.min

fun SvgMacro.RectGrid.draw(drawer: CompositionDrawer) {
    drawer.stroke = ColorRGBa.GREEN
    drawer.fill = ColorRGBa.BLACK
    drawer.rectangle(
        Rectangle(
            -Vector2(
                width / 2,
                height / 2
            ),
            width,
            height
        )
    )
    drawGrid(drawer)
}

private fun SvgMacro.RectGrid.drawGrid(drawer: CompositionDrawer) {
    val marginAbs = min(width, height) * margin
    val innerDim = Vector2(
        width - circleRadius * 2 - marginAbs * 2,
        height - circleRadius * 2 - marginAbs * 2
    )
    (0 until countX).forEach { xi ->
        (0 until countY).forEach { yi ->
            drawer.circle(
                -innerDim * 0.5 +
                        Vector2(
                            xi * innerDim.x / (countX - 1),
                            yi * innerDim.y / (countY - 1)
                        ), circleRadius
            )
        }
    }
}
