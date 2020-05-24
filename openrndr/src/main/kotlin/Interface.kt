import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Interface(
    var center: Vector2,
    var angle: Double,
    var length: Double,
    var terminals: Int) {

    fun draw(drawer: Drawer) {
        val (end1, end2) = getEnds()
        val line = LineSegment(end1, end2)
        drawer.lineSegment(line)
        when (terminals) {
            0 -> {}
            1 -> drawer.circle(center, 4.0)
            else ->  {
                drawer.circles(
                    line.contour.equidistantPositions(terminals),4.0)
            }
        }
    }

    internal fun getEnds(): List<Vector2> {
        val vec = Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        return listOf(center - vec * (length / 2), center + vec * (length / 2))
    }

    fun clone(): Interface {
        return Interface(center, angle, length, terminals)
    }
}
