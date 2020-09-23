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
                width.value / 2,
                height.value / 2
            ),
            width.value,
            height.value
        )
    )
    drawGrid(drawer)
}

private fun SvgMacro.RectGrid.drawGrid(drawer: CompositionDrawer) {
    val marginAbs = min(width.value, height.value) * margin
    val innerDim = Vector2(
        width.value - circleRadius.value * 2 - marginAbs * 2,
        height.value - circleRadius.value * 2 - marginAbs * 2
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
