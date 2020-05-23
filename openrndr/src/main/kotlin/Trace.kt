import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.SegmentJoin
import org.openrndr.shape.contours

class Trace(var points: MutableList<Vector2> = mutableListOf<Vector2>()) {

    constructor(points: Iterable<Vector2>) : this(points.toMutableList())

    fun withPoint(point: Vector2): Trace {
        return Trace(points + point)
    }

    fun draw(drawer: Drawer) {
        val cs = contours {
            points.forEachIndexed { i, point ->
                if (i == 0) moveTo(point)
                else lineTo(point)
            }
        }
        if (cs.isNotEmpty()) {
            val c = cs.first()
            drawer.contour(c)
            drawer.contour(c.offset(15.0, joinType = SegmentJoin.MITER))
            drawer.contour(c.offset(-15.0, joinType = SegmentJoin.MITER))
        }
    }
}

fun snapTo45(firstPoint: Vector2, secondPoint: Vector2) : Vector2 {
    return secondPoint
}
