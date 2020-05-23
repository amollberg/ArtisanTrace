import org.openrndr.draw.Drawer
import org.openrndr.math.Matrix33
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.shape.SegmentJoin
import org.openrndr.shape.contours
import kotlin.math.*

class Trace(var points: MutableList<Vector2> = mutableListOf<Vector2>()) {

    constructor(points: Iterable<Vector2>) : this(points.toMutableList())

    fun withPoint(point: Vector2): Trace {
        return Trace(points + point)
    }

    fun draw(drawer: Drawer) {
        val cs = contours {
            points.forEachIndexed { i, point ->
                if (i == 0) moveTo(point)
                else lineTo(point)
            }
        }
        if (cs.isNotEmpty()) {
            val c = cs.first()
            drawer.contour(c)
            drawer.contour(c.offset(15.0, joinType = SegmentJoin.MITER))
            drawer.contour(c.offset(-15.0, joinType = SegmentJoin.MITER))
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

    fun getKnee() = knee

    private fun recalculate() {
        var vec = end - start
        val (x, y) = vec
        val kneepoints = listOf(
            Vector2(x-y, 0.0),
            Vector2(x, 0.0),
            Vector2(x+y, 0.0),

            Vector2(0.0, y-x),
            Vector2(0.0, y),
            Vector2(0.0, y+x),

            Vector2(-y, y),
            Vector2((x-y)/2, (y-x)/2),
            Vector2(x, -x),

            Vector2(y, y),
            Vector2((x+y)/2, (x+y)/2),
            Vector2(x, x)
        )
        fun angleOf(point: Vector2): Int {
            val origin = Vector2.ZERO
            val a1 = arg(origin - point)
            val a2 = arg(vec - point)
            return abs((a1 - a2).toInt() % 360)
        }
        fun pred(kneepoint: Vector2): Boolean {
            val a = 180 - abs(angleOf(kneepoint) - 180)
            println("$kneepoint : $a")
            println(a < 90)
            println(a == 90)
            println(a > 90)
            return when (angle) {
                Angle.ACUTE -> a < 90
                Angle.RIGHT -> a == 90
                Angle.OBTUSE -> a > 90
            }
        }
        val relativeKnee = kneepoints.first { kp ->
            val matches = pred(kp)
            println("$kp $matches")
            matches
        }
        knee = start + relativeKnee
        println("Selected $knee")
    }
}

class Matrix22(val c0r0: Double = 0.0, val c1r0: Double = 0.0,
               val c0r1: Double = 0.0, val c1r1: Double = 0.0) {
    companion object {
        val IDENTITY = Matrix22(c0r0 = 1.0, c1r1 = 1.0)
    }

    constructor(c0r0: Int = 0, c1r0: Int = 0,
                c0r1: Int = 0, c1r1: Int = 0) :
            this(c0r0.toDouble(), c1r0.toDouble(),
                 c0r1.toDouble(), c1r1.toDouble())

    private var m = Matrix33(c0r0, c1r0, 0.0,
                             c0r1, c1r1, 0.0,
                             0.0,0.0, 1.0)

    operator fun times(v: Vector2) = (m * v.xy0).xy

    operator fun times(o: Matrix22) : Matrix22 {
        var product3 = (m * o.m)
        var product = Matrix22.IDENTITY
        product.m = product3
        return product
    }

    fun invert() : Matrix22 {
        val det = c0r0 * c1r1 - c0r1 * c1r0
        if (det == 0.0) {
            throw IllegalArgumentException(
                "Singular matrix $this cannot be inverted")
        }
        val deti = 1/det
        return Matrix22(deti * c1r1, -deti * c1r0,
                        - deti * c0r1, deti * c0r0)
    }
}

fun fold(v: Vector2, foldMatrix: Matrix22) : Vector2 {
    return foldMatrix.times(v)
}

fun unfold(v: Vector2, foldMatrix: Matrix22) : Vector2 {
    return foldMatrix.invert().times(v)
}

fun snapTo45(firstPoint: Vector2, secondPoint: Vector2) : Vector2 {
    var vec = secondPoint - firstPoint
    var foldings = mutableListOf<Matrix22>()
    // Folding
    if (vec.x < 0) {
        foldings.add(Matrix22(-1, 0, 0, 1))
        vec = fold(vec, foldings.last())
    }
    if (vec.y < 0) {
        foldings.add(Matrix22(1, 0, 0, -1))
        vec = fold(vec, foldings.last())
    }
    if (vec.x < vec.y) {
        foldings.add(Matrix22(0, 1, 1, 0))
        vec = fold(vec, foldings.last())
    }

    // Snap inside 45 degree sector
    if (vec.x == 0.0) {
        // Do nothing
    }
    else if (vec.y/vec.x < tan(45 / 2.0 * Math.PI / 180)) {
        // Project to x-axis
        vec = Vector2(vec.x, 0.0)
    }
    else {
        // Project to diagonal
        val c = (vec.x + vec.y)/2
        vec = Vector2(c, c)
    }

    // Undo folding
    foldings.reversed().forEach {
        vec = unfold(vec, it)
    }
    return firstPoint + vec
}
