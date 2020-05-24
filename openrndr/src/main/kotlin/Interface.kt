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
    var terminalCount: Int) {

    fun draw(drawer: Drawer) {
        val (end1, end2) = getEnds()
        val line = LineSegment(end1, end2)
        drawer.lineSegment(line)
        drawer.circles(
            (0 until terminalCount).map { getTerminalPosition(it) },
            4.0)
    }

    fun getTerminals(): Terminals {
        return Terminals(this, 0 until terminalCount)
    }

    fun getTerminalPosition(terminalIndex: Int): Vector2 {
        assert(terminalIndex < terminalCount)
        val (end1, end2) = getEnds()
        val line = LineSegment(end1, end2)
        return when (terminalCount) {
            1 -> center
            else -> line.contour
                .equidistantPositions(terminalCount)[terminalIndex]
        }
    }

    internal fun getEnds(): List<Vector2> {
        val vec = Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        return listOf(center - vec * (length / 2), center + vec * (length / 2))
    }

    fun clone(): Interface {
        return Interface(center, angle, length, terminalCount)
    }
}
