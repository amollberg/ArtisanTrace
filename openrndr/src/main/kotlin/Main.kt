import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.SegmentJoin.MITER
import org.openrndr.shape.contour

fun offsetPoly(drawer: Drawer, points: List<Vector2>) {
    if (points.size > 1) {
        val c = contour {
            points.forEachIndexed { i, point ->
                if (i == 0) moveTo(point)
                else lineTo(point)
            }
        }
        drawer.contour(c)
        drawer.contour(c.offset(15.0, joinType = MITER))
        drawer.contour(c.offset(-15.0, joinType = MITER))
    }
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        var points = mutableListOf<Vector2>()
        mouse.clicked.listen {
            points.add(it.position)
        }
        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            offsetPoly(drawer, points)
        }
    }
}