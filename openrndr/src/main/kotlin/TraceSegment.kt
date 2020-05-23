import org.openrndr.math.Vector2
import kotlin.math.*

/** A segment of a trace composed of two straight lines with a 45 or 90 degree
 *  corner.
 */
class TraceSegment(
    private var start: Vector2, private var end: Vector2, val angle: Angle) {
    lateinit private var knee: Vector2

    init {
        recalculate()
    }

    fun setEnd(position: Vector2) {
        end = position
        recalculate()
    }

    fun setStart(position: Vector2) {
        start = position
        recalculate()
    }

    fun getStart() = start
    fun getKnee() = knee
    fun getEnd() = end

    private fun recalculate() {
        var vec = end - start
        val (x, y) = vec
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
            val a2 = arg(vec - point)
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
        knee = start + relativeKnee
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
