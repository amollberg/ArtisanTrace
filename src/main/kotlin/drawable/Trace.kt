import coordinates.Coordinate
import coordinates.System
import kotlinx.serialization.Serializable
import org.openrndr.math.Vector2
import kotlin.math.tan

@Serializable
data class Trace(
    var system: System,
    private var traceSegments: MutableList<TraceSegment> = mutableListOf(),
    override var groupId: Int = -1,
    override var groupOrdinal: Int = -1
) : GroupMember {
    init {
        setSystem()
    }

    val segments: MutableList<TraceSegment> get() = traceSegments

    override val origin: Coordinate
        get() = traceSegments.firstOrNull()?.firstStartPosition()
            ?: system.originCoord

    constructor(system: System, points: Iterable<TraceSegment>) :
            this(system, points.toMutableList())

    fun add(segment: TraceSegment) {
        if (traceSegments.isNotEmpty()) {
            assert(segment.getStart() == traceSegments.last().getEnd())
        }
        traceSegments.add(segment)
        setSystem()
    }

    fun replace(segment: TraceSegment, replacements: List<TraceSegment>) {
        traceSegments = traceSegments.flatMap {
            if (it == segment) replacements
            else listOf(it)
        }.toMutableList()
        setSystem()
    }

    override fun draw(drawer: OrientedDrawer) {
        traceSegments.forEach { it.draw(drawer) }
    }

    fun withSegment(segment: TraceSegment) =
        Trace(system, traceSegments + segment)

    private fun setSystem() {
        traceSegments.forEach {
            it.system = system
        }
    }

    fun setCoordinateSystem(modelSystem: System) {
        system = modelSystem
        setSystem()
    }
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
