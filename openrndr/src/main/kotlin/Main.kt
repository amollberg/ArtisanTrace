import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.SegmentJoin.MITER
import org.openrndr.shape.contours

fun offsetPoly(drawer: Drawer, points: List<Vector2>, mousePoint: Vector2) {
    val allPoints = points + mousePoint
    val cs = contours {
        allPoints.forEachIndexed { i, point ->
            if (i == 0) moveTo(point)
            else lineTo(point)
        }
    }
    if (cs.isNotEmpty()) {
        val c = cs.first()
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
        var mousePoint = Vector2(-1.0, -1.0)

        mouse.moved.listen {
            mousePoint = it.position
        }
        mouse.clicked.listen {
            points.add(it.position)
        }
        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            offsetPoly(drawer, points, mousePoint)
        }
    }
}