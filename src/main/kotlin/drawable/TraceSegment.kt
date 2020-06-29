@file:UseSerializers(Vector2Serializer::class)

import coordinates.Coordinate
import coordinates.Length
import coordinates.System
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Vector2
import org.openrndr.shape.LineSegment
import org.openrndr.shape.intersection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

/** A segment of a trace composed of two straight lines with a 45 degree
 * corner (the knee).
 */
@Serializable
data class TraceSegment(
    internal var start: Terminals,
    internal var end: Terminals,
    val angle: Angle,
    var reverseKnee: Boolean,
    @Transient
    var system: System = System.root()
) {
    val bounds: Poly
        get() = bounds(
            start.hostInterface.getEnds(),
            end.hostInterface.getEnds()
        )

    fun getStart() = start
    fun getEnd() = end

    fun getKnee() = Companion.getKnee(
        firstStartPosition(), firstEndPosition(), system, angle, reverseKnee
    )

    fun getKnees() = splitIntoSingleLeads().map { it.getKnee() }

    fun lineSegments(system: System) = splitIntoSingleLeads().flatMap {
        listOf(
            LineSegment(
                it.firstStartPosition().xyIn(system),
                it.getKnee().xyIn(system)
            ),
            LineSegment(
                it.getKnee().xyIn(system),
                it.firstEndPosition().xyIn(system)
            )
        )
    }

    fun draw(drawer: OrientedDrawer) {
        drawer.drawer.lineSegments(lineSegments(drawer.system))
    }

    fun firstStartPosition() =
        start.hostInterface.getTerminalPosition(start.range.first)

    fun firstEndPosition() =
        end.hostInterface.getTerminalPosition(end.range.first)

    private fun splitIntoSingleLeads(): List<TraceSegment> =
        start.range.zip(end.range).map { (startTerminal, endTerminal) ->
            TraceSegment(
                Terminals(
                    start.hostInterface,
                    startTerminal..startTerminal
                ),
                Terminals(
                    end.hostInterface,
                    endTerminal..endTerminal
                ),
                angle,
                reverseKnee,
                system
            )
        }

    fun bounds(startEnds: List<Coordinate>, endEnds: List<Coordinate>): Poly {
        val (startFirst, startLast) = startEnds
        val (endFirst, endLast) = endEnds
        val kneeFirst = Companion.getKnee(
            startFirst,
            endFirst,
            system,
            angle,
            reverseKnee
        )
        val kneeLast = Companion.getKnee(
            startLast,
            endLast,
            system,
            angle,
            reverseKnee
        )
        // Some non-zero margin, to accept collinear segments
        val margin = 1e-4
        // If the interfaces are oriented such that the traces cross each other
        val isTwisted = doIntersect(
            LineSegment(startFirst.xyIn(system), kneeFirst.xyIn(system)),
            LineSegment(kneeLast.xyIn(system), endLast.xyIn(system)),
            margin
        ) || doIntersect(
            LineSegment(kneeFirst.xyIn(system), endFirst.xyIn(system)),
            LineSegment(startLast.xyIn(system), kneeLast.xyIn(system)),
            margin
        )
        return when (isTwisted) {
            // Untwist by reversing the end ends
            true -> bounds(startEnds, endEnds.reversed())
            false -> Poly(
                listOf(
                    startFirst,
                    kneeFirst,
                    endFirst,
                    endLast,
                    kneeLast,
                    startLast
                )
            )
        }
    }

    companion object {
        fun getKnee(
            start: Coordinate,
            end: Coordinate,
            system: System,
            angle: Angle,
            reverseKnee: Boolean
        ): Coordinate {
            val (startPosition, endPosition) =
                if (reverseKnee) Pair(end, start)
                else Pair(start, end)

            var vec = endPosition - startPosition
            val (x, y) = vec.xyIn(system)
            val kneepoints = listOf(
                Vector2(x - y, 0.0),
                Vector2(x, 0.0),
                Vector2(x + y, 0.0),

                Vector2(0.0, y - x),
                Vector2(0.0, y),
                Vector2(0.0, y + x),

                Vector2(-y, y),
                Vector2((x - y) / 2, (y - x) / 2),
                Vector2(x, -x),

                Vector2(y, y),
                Vector2((x + y) / 2, (x + y) / 2),
                Vector2(x, x)
            )

            fun angleOf(point: Vector2): Int {
                val origin = Vector2.ZERO
                val a1 = arg(origin - point)
                val a2 = arg(vec.xyIn(system) - point)
                return abs((a1 - a2).toInt() % 360)
            }

            val relativeKnee = kneepoints.filter { kneepoint ->
                val a = 180 - abs(angleOf(kneepoint) - 180)
                when (angle) {
                    Angle.ACUTE -> a < 90
                    Angle.RIGHT -> a == 90
                    Angle.OBTUSE -> a > 90
                }
            }.getOrElse(0, { Vector2.ZERO })

            return startPosition + Length(relativeKnee, system)
        }
    }
}

/** Return the counter-clockwise angle from positive x-axis to xy in degrees,
 *  -180 to 180
 */
fun arg(p: Vector2): Double {
    return 180 / PI * atan2(p.y, p.x)
}

enum class Angle {
    ACUTE,
    RIGHT,
    OBTUSE
}

fun doIntersect(a: LineSegment, b: LineSegment, endMargin: Double = 0.0):
        Boolean {
    val point = intersection(a, b)

    return a.sub(endMargin, 1 - endMargin).distance(point) == 0.0 &&
            b.sub(endMargin, 1 - endMargin).distance(point) == 0.0
}
