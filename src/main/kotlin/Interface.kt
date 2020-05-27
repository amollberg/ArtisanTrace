@file:UseSerializers(Vector2Serializer::class)

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.serialization.*
import org.openrndr.shape.ShapeContour

@Serializable
data class Interface(
    var center: Vector2,
    var angle: Double,
    var length: Double,
    var terminalCount: Int,
    // Only used for serialization
    internal var id: Int = -1) {

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
            else ->
                equidistantPositionsForcedNumber(
                        line.contour, terminalCount)[terminalIndex]
        }
    }

    internal fun getEnds(): List<Vector2> {
        val vec = Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        return listOf(center - vec * (length / 2), center + vec * (length / 2))
    }

    fun withTerminalCount(newCount: Int): Interface {
        val itf = clone()
        itf.length = when (newCount) {
            // Distance covered by 3 terminals
            1 -> length * newCount / (terminalCount - 1)
            else -> length * (newCount - 1) / (terminalCount - 1)
        }
        itf.terminalCount = newCount
        return itf
    }

    fun clone(): Interface {
        return Interface(center, angle, length, terminalCount)
    }
}

internal fun equidistantPositionsForcedNumber(
        contour: ShapeContour, count: Int): List<Vector2> {
    return if (contour.length == 0.0) {
        // Use same positions for all points
        List(count, { contour.segments.first().start })
    } else {
        contour.equidistantPositions(count)
    }
}
