import kotlinx.serialization.Serializable
import org.openrndr.math.Vector2
import kotlin.math.tan

@Serializable
data class Trace(var segments: MutableList<TraceSegment> = mutableListOf()) {

    constructor(points: Iterable<TraceSegment>) :
            this(points.toMutableList())

    fun add(segment: TraceSegment) {
        if (segments.isNotEmpty()) {
            assert(segment.getStart() == segments.last().getEnd())
        }
        segments.add(segment)
    }

    fun draw(drawer: OrientedDrawer) {
        segments.forEach { it.draw(drawer) }
    }

    fun withSegment(segment: TraceSegment) = Trace(segments + segment)
}

fun fold(v: Vector2, foldMatrix: Matrix22): Vector2 {
    return foldMatrix.times(v)
}

fun unfold(v: Vector2, foldMatrix: Matrix22): Vector2 {
    return foldMatrix.invert().times(v)
}

fun snapTo45(firstPoint: Vector2, secondPoint: Vector2): Vector2 {
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
    } else if (vec.y / vec.x < tan(45 / 2.0 * Math.PI / 180)) {
        // Project to x-axis
        vec = Vector2(vec.x, 0.0)
    } else {
        // Project to diagonal
        val c = (vec.x + vec.y) / 2
        vec = Vector2(c, c)
    }

    // Undo folding
    foldings.reversed().forEach {
        vec = unfold(vec, it)
    }
    return firstPoint + vec
}
