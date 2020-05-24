import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Interface(
    val center: Vector2,
    val angle: Double,
    val length: Double,
    val terminals: Int) {

    fun draw(drawer: Drawer) {
        val (end1, end2) = getEnds()
        drawer.lineSegment(end1, end2)
    }

    internal fun getEnds(): List<Vector2> {
        val vec = Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        return listOf(center - vec * (length / 2), center + vec * (length / 2))
    }
}
