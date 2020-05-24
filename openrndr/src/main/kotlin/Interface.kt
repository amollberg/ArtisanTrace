import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
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
        drawer.lineSegment(end1, end2)
    }

    internal fun getEnds(): List<Vector2> {
        val vec = Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        return listOf(center - vec * (length / 2), center + vec * (length / 2))
    }

    fun clone(): Interface {
        return Interface(center, angle, length, terminals)
    }
}
