@file:UseSerializers(Vector2Serializer::class)

import coordinates.Coordinate
import coordinates.Length
import coordinates.System
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import org.openrndr.math.Vector2
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
    val angle: Angle,
    var reverseKnee: Boolean,
    @Transient
    var system: System = System.root()
) {
    fun getStart() = start
    fun getEnd() = end

    fun getKnee(): Coordinate {
        val (startPosition, endPosition) =
            if (reverseKnee) Pair(firstEndPosition(), firstStartPosition())
            else Pair(firstStartPosition(), firstEndPosition())

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
                // Workaround for fill color showing up in the convex hull
                isolatedStyle(
                    drawer.drawer,
                    fill = ViewModel.DEFAULT_STYLE.background
                ) {
                    it.contour(cs.first())
                }
            }
        }
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
