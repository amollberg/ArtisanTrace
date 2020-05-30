@file:UseSerializers(Vector2Serializer::class)

import coordinates.Coordinate
import coordinates.Length
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Vector2
import org.openrndr.shape.SegmentJoin
import org.openrndr.shape.contours
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
    val angle: Angle
) {
    fun getStart() = start
    fun getEnd() = end

    fun getKnee(): Coordinate {
        var vec = firstEndPosition() - firstStartPosition()
        val (x, y) = vec.xy()
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
            val a2 = arg(vec.xy() - point)
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

        return firstStartPosition() + Length(relativeKnee, vec.system)
    }

    fun getKnees() = splitIntoSingleLeads().map { it.getKnee() }

    fun draw(drawer: OrientedDrawer) {
        if (start.count() > 1) {
            // Divide segment into one per lead
            splitIntoSingleLeads().forEach { it.draw(drawer) }
        } else {
            val cs = contours {
                moveTo(firstStartPosition().xy(drawer))
                lineTo(getKnee().xy(drawer))
                lineTo(firstEndPosition().xy(drawer))
            }
            if (cs.isNotEmpty()) {
                val c = cs.first()
                (0 until start.count()).forEach { i ->
                    drawer.drawer.contour(
                        c.offset(10.0 * i, SegmentJoin.MITER)
                    )
                }
            }
        }
    }

    private fun recalculate() {

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
                angle
            )
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
