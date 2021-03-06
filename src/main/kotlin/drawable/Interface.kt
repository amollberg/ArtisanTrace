import coordinates.Coordinate
import coordinates.System
import kotlinx.serialization.Serializable
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import org.openrndr.shape.ShapeContour
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Serializable
data class Interface(
    var center: Coordinate,
    // Rotation of interface clockwise from positive X axis
    var angle: Double,
    var length: Double,
    var terminalCount: Int,
    // Only used for serialization
    internal var id: Int = -1,
    override var groupId: Int = -1,
    override var groupOrdinal: Int = -1
) : GroupMember() {
    private val system get() = center.system

    override fun draw(drawer: OrientedDrawer) {
        val (end1, end2) = getEnds().map { it.relativeTo(drawer.system) }
        val line = LineSegment(end1.xy(), end2.xy())
        drawer.drawer.lineSegment(line)
        drawer.drawer.circles(
            (0 until terminalCount).map {
                getTerminalPosition(it).relativeTo(drawer.system).xy()
            },
            1.5
        )
    }

    override val bounds get() = Poly(getEnds())

    fun getTerminals(): Terminals {
        return Terminals(this, 0 until terminalCount)
    }

    fun getTerminalPosition(terminalIndex: Int): Coordinate {
        assert(terminalIndex < terminalCount)
        val (end1, end2) = getEnds()
        val line = LineSegment(end1.xy(), end2.xy())
        return when (terminalCount) {
            1 -> center
            else ->
                system.coord(
                    equidistantPositionsForcedNumber(
                        line.contour, terminalCount
                    )[terminalIndex]
                )
        }
    }

    fun getConnectedTraces(model: Model) =
        model.getTracesRecursively().filter {
            it.segments.any {
                it.start.hostInterface === this || it.end.hostInterface === this
            }
        }

    fun getConnectedSegments(terminalIndex: Int, model: Model) =
        model.getTracesRecursively().flatMap {
            it.segments.filter {
                (it.start.hostInterface === this && it.start.range.contains(
                    terminalIndex
                )) || (it.end.hostInterface === this && it.end.range.contains(
                    terminalIndex
                ))
            }
        }

    internal fun getEnds(): List<Coordinate> {
        val vec = system.length(
            Vector2(cos(angle * PI / 180), sin(angle * PI / 180))
        )
        return listOf(
            center - vec * (length / 2),
            center + vec * (length / 2)
        )
    }

    fun lengthIn(system: System) =
        getEnds().let { (end1, end2) -> (end1 - end2).lengthIn(system) }

    fun withTerminalCount(newCount: Int): Interface {
        val itf = clone()
        itf.length = when (newCount) {
            1 -> terminalStride()
            else -> (newCount - 1) * terminalStride()
        }
        itf.terminalCount = newCount
        return itf
    }

    internal fun terminalStride(): Double {
        return when (terminalCount) {
            1 -> length
            else -> length / (terminalCount - 1)
        }
    }

    fun clone(): Interface {
        return Interface(center, angle, length, terminalCount)
    }
}

internal fun equidistantPositionsForcedNumber(
    contour: ShapeContour, count: Int
): List<Vector2> {
    return if (contour.length == 0.0) {
        // Use same positions for all points
        List(count, { contour.segments.first().start })
    } else {
        contour.equidistantPositions(count)
    }
}
